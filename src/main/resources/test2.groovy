


import org.apache.commons.dbcp.BasicDataSource
import org.springframework.batch.core.launch.support.SimpleJobLauncher
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider
import org.springframework.batch.item.database.JdbcBatchItemWriter
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper
import org.springframework.batch.item.file.mapping.DefaultLineMapper
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer
import org.springframework.context.support.GenericGroovyApplicationContext
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import packageTest.PersonItemProcessor
import packageTest.StepExecutionStatsListener

import javax.sql.DataSource

/**
 * Created by Nojpg on 23.09.17.
 */

def context = new GenericGroovyApplicationContext()
//
context.reader.beans {
//    xmlns( [batch: 'http://www.springframework.org/schema/batch'], [jdbc: 'http://www.springframework.org/schema/jdbc'], [p: 'http://www.springframework.org/schema/p'])


    xmlns batch: 'http://www.springframework.org/schema/batch'
    xmlns p: 'http://www.springframework.org/schema/p'
    xmlns jdbc: 'http://www.springframework.org/schema/jdbc'
    batch.job(id: 'personJob'){
        batch.step(id: 'step1'){
            batch.tasklet{
                batch.chunk(
                        reader: 'itemReader',
                        processor: 'itemProcessor',
                        writer: 'itemWriter',
                        'commit-interval': 10
                )
                batch.listeners(){
                    batch.'listener'(ref: 'stepExecutionListener')
                }
            }
            batch.fail(on: 'FAILED')
            batch.end(on: '*')
        }
    }
    jdbc.'embedded-database'(id: 'dataSource', type:"H2"){
        jdbc.script(location: "classpath:/org/springframework/batch/core/schema-h2.sql")
        jdbc.script(location: "/META-INF/spring/jobs/personJob/support/person.sql")
    }

    batch.'job-repository'(id: 'jobRepository')

    jobLauncher(
            class: 'SimpleJobLauncher',
            p.'jobRepository-ref'= 'jobRepository'
    )


    itemProcessor(PersonItemProcessor)

    stepExecutionListener(StepExecutionStatsListener)

    itemReader(FlatFileItemReader){
        resource = 'classpath:/META-INF/spring/jobs/personJob/support/test-data.csv'
        lineMapper = {
            DefaultLineMapper mapper ->
                lineTokenizer = {
                    DelimitedLineTokenizer tokenizer ->
                        names = ['firstName', 'lastName']
                }
                fieldSetMapper = {
                    BeanWrapperFieldSetMapper fieldSetMapper ->
                        targetType = 'packageTest.Person'
                }
        }
    }

    itemWriter(JdbcBatchItemWriter){
        def bean ->
            itemSqlParameterSourceProvider = BeanPropertyItemSqlParameterSourceProvider provider
            sql = "INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)"
            dataSource = ref('dataSource')

    }
    transactionManager(DataSourceTransactionManager, p.'dataSource-ref' = ref('dataSource')){

    }




}

