package io.github.taoganio.datasource.provider.test;

import io.github.taoganio.datasource.provider.test.service.*;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.SQLException;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
class DataSourceProviderTests {

    static {
        System.setProperty("java.naming.factory.initial", "org.apache.naming.java.javaURLContextFactory");
        System.setProperty("java.naming.factory.url.pkgs", "org.apache.naming");
    }

    @Autowired
    private MongodbSwitchService mongodbSwitchService;

    @Autowired
    private MongodbSwitchNestedService mongodbSwitchNestedService;

    @Autowired
    private JdbcService jdbcService;

    @Autowired
    private SeataService seataService;

    @Autowired
    private AtomikosService atomikosService;

    @Autowired
    private AnnotationService annotationService;

    @Autowired
    private SpelKeyService spelService;

    @Test
    void mongodbDSTest() {
        String terminfo = mongodbSwitchService.getTerminfo();
        System.out.println(terminfo);
//        String certImage = mongodbSwitchService.getCertImage();
//        System.out.println(certImage);
//        String qqFriend2 = mongodbSwitchNestedService.getQQFriend2();
//        System.out.println(qqFriend2);
    }

    @Test
    public void jdbcDSTest() {
        jdbcService.selectUser();
        jdbcService.selectCustomer();
        jdbcService.selectSite();
    }

    @Test
    public void seataDataSourceTest() throws SQLException {
        seataService.update();
    }

    @Test
    void atomikosTest() {
        atomikosService.update();
    }

    @Test
    void annotationTest() {
        annotationService.selectUser1();
//        annotationService.selectUser2();
    }

    @Test
    void spelTest() {
        Random random = new Random();
//        spelService.selectByDb(random.nextInt(50));
//        spelService.selectByDb(random.nextInt(100));
//        spelService.selectByDb(random.nextInt(1));
//        spelService.selectByDb(random.nextInt(2));
        spelService.selectMongoByDb("master","202501");
    }
}
