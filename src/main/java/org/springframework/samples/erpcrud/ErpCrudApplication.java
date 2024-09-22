/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.erpcrud;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.List;
import java.util.Map;

/**
 * PetClinic Spring Boot Application. -Dspring.profiles.active=postgres
 *
 * @author Dave Syer requestMatchers("/").hasAnyRole("USER")
 * https://github.com/spring-tips/vector-databases/tree/main
 * #https://spring.io/guides/gs/securing-web
 * https://medium.com/@vishal.sharma./run-postgresql-and-pgadmin-using-docker-compose-34120618bcf9
 */
@SpringBootApplication(scanBasePackages = "org.springframework.samples")
@ImportRuntimeHints(PetClinicRuntimeHints.class)
public class ErpCrudApplication {

	public static void main(String[] args) {
		SpringApplication.run(ErpCrudApplication.class, args);
	}

	@Bean
	@Profile("postgres")
	TokenTextSplitter tokenTextSplitter() {
		return new TokenTextSplitter();
	}

	private final boolean ingest = true;

	@Bean
	@Profile("postgres")
	ApplicationRunner demo(TokenTextSplitter tokenTextSplitter, JdbcClient db, VectorStore vectorStore) {
		return args -> {

			db.sql("delete from vector_store").update();
			var products = db.sql("select * from owners").query(new DataClassRowMapper<>(Product.class)).list();

			if (this.ingest) {

				products.parallelStream().forEach(product -> {
					var document = new Document("Name is " + product.first_name() + " " + product.last_name()+ " Address is " + product.address()+" Telephone is "+product.telephone(),
							Map.of("phone", product.telephone(), "id", product.id(), "firstname", product.first_name(),
									"lastname", product.last_name(), "address", product.address()));

					var split = tokenTextSplitter.apply(List.of(document));
					vectorStore.add(split);
				});

			}

			// var single = products
			// .parallelStream()
			// .filter(product -> product.id() == 115)
			// .toList()
			// .getFirst();

			var similar = vectorStore.similaritySearch(SearchRequest.defaults().query("Fair Way").withTopK(10));
			System.out.println("count: " + similar.size());
			for (var s : similar) {
				System.out.println("===========");
				var id = (s.getMetadata().get("id"));
				System.out.println("id: " + id);
				s.getMetadata().forEach((k, v) -> System.out.println(k + '=' + v));
			}

		};
	}

}

record Product(int id, String first_name, String last_name, String address, String telephone) {
}