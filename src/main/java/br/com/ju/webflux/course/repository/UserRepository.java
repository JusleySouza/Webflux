package br.com.ju.webflux.course.repository;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import br.com.ju.webflux.course.entity.User;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
@Generated
public class UserRepository {
	
	private final ReactiveMongoTemplate mongoTemplate;
	
	public Mono<User> save(final User user){
		return mongoTemplate.save(user);
	}

	public Mono<User> findById(String id) {
		return mongoTemplate.findById(id, User.class);
	}

	public Flux<User> findAll() {
		return mongoTemplate.findAll(User.class);
	}

	public Mono<User> findAndRemove(String id) {
		Query query = new Query();
		Criteria where = Criteria.where("id").is(id);
		return mongoTemplate.findAndRemove(query.addCriteria(where), User.class);
	}

}
