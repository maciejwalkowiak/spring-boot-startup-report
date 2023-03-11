package com.maciejwalkowiak.spring.boot.startup;

import com.demo.App;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = App.class)
class ReportRendererTests {

    @Autowired
    private ReportRenderer reportRenderer;

    @Test
    void rendersReport() {
        String report = reportRenderer.render();
        assertThat(report).contains("const data = [{");
    }

}
