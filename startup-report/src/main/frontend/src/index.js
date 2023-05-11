const d3 = require("d3v4");
const {h, text, app} = require("hyperapp");
const tip = require("d3-tip").default;
const {flamegraph} = require("d3-flame-graph");

function ready(fn) {
  if (document.readyState !== 'loading') {
    fn();
  } else {
    document.addEventListener('DOMContentLoaded', fn);
  }
}

ready(function() {
    let data = window.data;

    // flame chart
    const sortedByDuration = [...data];
    sortByDuration(sortedByDuration);

    // if there is a single top level element, show directly its children
    if (data.length == 1) {
        data = data[0].children;
    }

    function compare(a,b) {
        if (a.duration < b.duration) {
            return -1;
        }
        if (a.duration > b.duration) {
            return 1;
        }
        return 0;
    }

    function sortByDuration(events) {
        events.sort(compare);
        events.forEach(node => {
            if (node.children.length > 0) {
                sortByDuration(node.children);
            }
        })
    }

    const chart = flamegraph()
        .width(document.getElementById("chart").offsetWidth - 100);

    const tooltip = tip()
        .direction("s")
        .offset([8, 0])
        .attr('class', 'd3-flame-graph-tip')
        .html(function(d) { return JSON.stringify(d.data.tags) });

    chart.tooltip(tooltip);

    if (sortedByDuration.length > 0) {
        // there can be single top level element
        // educated guess that if there are more than one element, the first one is the one that contains interesting data
        d3.select("#chart")
            .datum(sortedByDuration[0])
            .call(chart);
    }

    document.getElementById("reset-zoom").addEventListener("click", e => {
        chart.resetZoom();
        e.preventDefault();
    })

    // table
    const colorNames = ['yellow', 'green'];
    const levels = [100, 200, 300, 400, 500];
    const colors = ['gray', ...colorNames.flatMap(color => levels.map(l => `${color}-${l}`))];

    const Details = (tags) =>
        h("ul", {}, Object.entries(tags).map(([k, v]) => h("li", {}, [
            h("strong", {}, text(k + ": ")),
            text(v)
        ])));

    const zoomOut = () =>
        h("div", {
            innerHTML: `
            <svg class="w-6 h-6"
             fill="none"
             stroke="currentColor"
             viewBox="0 0 24 24"
             xmlns="http://www.w3.org/2000/svg">
            <path stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0zM13 10H7"></path>
        </svg>`
        }, [])

    const zoomIn = () =>
        h("div", {
            innerHTML: `
            <svg xmlns="http://www.w3.org/2000/svg"
             fill="none"
             viewBox="0 0 24 24"
             stroke-width="1.5"
             stroke="currentColor"
             class="w-6 h-6">
            <path stroke-linecap="round"
                  stroke-linejoin="round"
                  d="M21 21l-5.197-5.197m0 0A7.5 7.5 0 105.196 5.196a7.5 7.5 0 0010.607 10.607zM10.5 7.5v6m3-3h-6"/>
        </svg>`
        }, [])

    const Placeholder = () =>
        h("div", {
            innerHTML: `&nbsp;`,
            style: {width: '24px'}
        }, [])

    const Row = (event, expandedEventIds, minDuration, search, level = 0) =>
        h("tbody", {}, [
            h("tr", {class: `cursor-pointer hover:bg-blue-200 bg-${colors[level]}`, onclick: [Expand, event]}, [
                h("td", {
                    class: "col-icon",
                }, event.children.length > 0 && expandedEventIds.indexOf(event.id) === -1 ? zoomIn() : expandedEventIds.indexOf(event.id) !== -1 ? zoomOut() : Placeholder()),
                h("td", {class: `col-name pl-${2 + level * 2}`}, text(event.name)),
                h("td", {class: "col-duration"}, text(event.value)),
                h("td", {class: "col-duration"}, text(event.actualDuration)),
                h("td", {class: "col-details text-sm"}, Details(event.tags))]
            ),
            h("tr", {}, [
                h("td", {colspan: 5, class: {hidden: expandedEventIds.indexOf(event.id) === -1}},
                    event.children
                        .filter(event => FilterEvent(event, minDuration, search))
                        .map(event => Row(event, expandedEventIds, minDuration, search, level + 1))
                )
            ])
        ]);

    const Expand = (state, event) => {
        if (event.children.length > 0 && state.expandedEventIds.indexOf(event.id) === -1) {
            return {...state, expandedEventIds: [event.id, ...state.expandedEventIds]};
        } else {
            return {...state, expandedEventIds: state.expandedEventIds.filter(it => it !== event.id)};
        }
    };

    const OnDurationChange = (state, target) => {
        return {...state, duration: target.value}
    }

    const OnSearchChange = (state, target) => {
        return {...state, search: target.value}
    }

    function hasMinimumDuration(event, minimumDuration) {
        return event.value >= minimumDuration;
    }

    function labelMatches(event, search) {
        return search === '' || event.name.toLowerCase().includes(search.toLowerCase()) || Object.entries(event.tags).map(([k, v]) => k+v).filter(it => it.toLowerCase().includes(search.toLowerCase())).length > 0;
    }

    function childrenMatch(event, minimumDuration, search) {
        return search === '' || event.children.filter(it => hasMinimumDuration(it, minimumDuration) && labelMatches(it, search)).length > 0;
    }

    const FilterEvent = (event, minimumDuration, search) => {
        return hasMinimumDuration(event, minimumDuration) && (labelMatches(event, search) || childrenMatch(event, minimumDuration, search));
    }

    app({
        init: [{events: data, expandedEventIds: [], duration: 0, search: ''}, []],
        view: (state) => h("div", {}, [
            h("div", {class: "p-4 w-full bg-gray-100 text-gray-700 top-header"}, [
                h("h2", {class: "text-3xl pt-4"}, text("🍃 Spring Boot Startup Analyzer")),
                h("h3", {class: "text-base font-light pb-8 pt-2 text-gray-100"}, [
                    h("span", {}, text("made by ")),
                    h("a", {href: "https://twitter.com/maciejwalkowiak", class: "hover:text-red-600 font-medium text-red-400"}, text("@maciejwalkowiak")),
                ]),
                h("div", {}, [
                    h("span", {
                        class: 'font-medium mr-2'
                    }, [ text("Minimum duration")]),
                    h("input", {
                        type: "text",
                        class: 'px-4 w-16 text-right text-gray-800',
                        value: state.duration,
                        oninput: (_, { target }) => [OnDurationChange, target]}, []),
                    h("span", {
                        class: 'font-medium mr-2 ml-4'
                    }, [ text("Search")]),
                    h("input", {
                        type: "text",
                        class: 'px-4 w-64 text-right text-gray-800',
                        value: state.search,
                        oninput: (_, { target }) => [OnSearchChange, target]}, [])
                ])
            ]),
            h("div", {class: "p-4 w-full bg-gray-400"}, [
                h("table", {class: "event-table"}, [
                    h("thead", {}, [
                        h("tr", {class: "font-medium"}, [
                            h("td", {class: "col-icon"}, text("")),
                            h("td", {class: "col-name"}, text("Name")),
                            h("td", {class: "col-duration"}, text("Duration with children (ms)")),
                            h("td", {class: "col-duration"}, text("Duration (ms)")),
                            h("td", {class: "col-details"}, text("Details"))
                        ])
                    ]),
                    ...state.events
                        .filter(event => FilterEvent(event, state.duration, state.search))
                        .map(event => Row(event, state.expandedEventIds, state.duration, state.search))
                ])
            ])]),
        node: document.getElementById("app"),
    });
});