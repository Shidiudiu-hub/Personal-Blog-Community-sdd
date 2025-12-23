package com.coding;

import com.coding.utils.GenerateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@ActiveProfiles("dev")
@Component
@SpringBootTest(classes = {FoodApp.class})
public class AutoGenerateAllTest {

    @Autowired
    private DataSourceProperties dataSourceProperties;

    @Test
    void generate() throws IOException {
        FileUtils.deleteDirectory(new File("target/java"));
        log.info("开始执行生成代码");
        List<String> list = Lists.newArrayList();
        list.add("user");
        // 添加其他表名
        GenerateUtil.generate(dataSourceProperties, list, "com.coding");
        GenerateUtil.generateAll(dataSourceProperties, list, "com.coding");
        log.info("生成完毕");
    }


}
