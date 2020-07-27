package com.github.wujun234.uid;

import com.github.wujun234.uid.impl.CachedUidGenerator;
import com.github.wujun234.uid.impl.DefaultUidGenerator;
import com.github.wujun234.uid.impl.UidDatasourceProperties;
import com.github.wujun234.uid.impl.UidProperties;
import com.github.wujun234.uid.worker.DisposableWorkerIdAssigner;
import com.github.wujun234.uid.worker.WorkerIdAssigner;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.sql.DataSource;

/**
 * UID 的自动配置
 *
 * @author wujun
 * @date 2019.02.20 10:57
 */
@Configuration
@ConditionalOnClass({DefaultUidGenerator.class, CachedUidGenerator.class})
@MapperScan(value = {"com.github.wujun234.uid.worker.dao"}, sqlSessionFactoryRef = "sqlSessionFactoryUid")
@EnableConfigurationProperties({UidProperties.class, UidDatasourceProperties.class})
public class UidAutoConfigure {

    @Autowired
    private UidProperties uidProperties;
    @Autowired
    private UidDatasourceProperties datasourceProperties;

    @Bean
    @ConditionalOnMissingBean
    @Lazy
    DefaultUidGenerator defaultUidGenerator() {
        return new DefaultUidGenerator(uidProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    @Lazy
    CachedUidGenerator cachedUidGenerator() {
        return new CachedUidGenerator(uidProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    WorkerIdAssigner workerIdAssigner() {
        return new DisposableWorkerIdAssigner();
    }

    @Bean(name = "uidDatasource")
    public DataSource uidDatasource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(datasourceProperties.getDriverClassName());
        dataSource.setUsername(datasourceProperties.getUsername());
        dataSource.setPassword(datasourceProperties.getPassword());
        dataSource.setJdbcUrl(datasourceProperties.getJdbcUrl());
        return dataSource;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactoryUid() throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(uidDatasource());
        return factoryBean.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplateUid() throws Exception {
        return new SqlSessionTemplate(sqlSessionFactoryUid());
    }
}
