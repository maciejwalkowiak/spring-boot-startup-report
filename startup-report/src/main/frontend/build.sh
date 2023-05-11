#!/bin/sh
set -euxo pipefail
tailwindcss -i src/main.css -m -o ../resources/META-INF/resources/startup-report/static/main.css
npx webpack --mode=production -o ../resources/META-INF/resources/startup-report/static/
cp node_modules/d3-flame-graph/dist/d3-flamegraph.css ../resources/META-INF/resources/startup-report/static/d3-flamegraph.css