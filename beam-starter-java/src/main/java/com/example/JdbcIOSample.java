package com.example;

import java.sql.ResultSet;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.jdbc.JdbcIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.values.TypeDescriptors;

public class JdbcIOSample {
  public interface Options extends PipelineOptions {
  }

  public static void main(String[] args) {
    PipelineOptions options = PipelineOptionsFactory.fromArgs(args).withValidation().as(Options.class);
    Pipeline pipeline = Pipeline.create(options);

    JdbcIO.DataSourceConfiguration configuration = JdbcIO.DataSourceConfiguration
      .create("org.postgresql.Driver", "jdbc:postgresql://localhost:5432/bmdb")
      .withUsername("postgres")
      .withPassword("postgres");

    pipeline
      .apply("Read from PostgreSQL", JdbcIO.<String>read()
        .withDataSourceConfiguration(configuration)
        .withQuery("select * from bm.user")
        .withFetchSize(100)
        .withRowMapper(new JdbcIO.RowMapper<String>() {
          @Override
          public String mapRow(ResultSet resultSet) throws Exception {
            return String.join(" ", resultSet.getString("user_id"), resultSet.getString("user_name"));
          }
        }))
      .apply("Print Rows", 
        MapElements.into(TypeDescriptors.strings()).via(x -> {
          System.out.println(x);
          return x;
        }));
    pipeline.run().waitUntilFinish();
  }
}
