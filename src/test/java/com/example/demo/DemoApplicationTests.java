package com.example.demo;

import com.example.demo.domain.Phone;
import com.example.demo.domain.QPhone;
import com.example.demo.domain.QUser;
import com.example.demo.domain.User;
import com.example.demo.repositories.PhoneRepository;
import com.example.demo.repositories.UserRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

@SpringBootTest
class DemoApplicationTests {

	@Autowired
	UserRepository userRepository;

	@Autowired
	PhoneRepository phoneRepository;

	@PersistenceContext
	EntityManager em;

	@BeforeEach
	public void init() {
		setup();
	}

	@Test
	void contextLoads() {

		//assert userRepository.count() == 2;
		QUser qUser = QUser.user;
		User user =userRepository.findAll(qUser.name.eq("John Doe")).iterator().next();
		assert user.getPhone().size() == 2;
		System.out.println("==========================");
		BooleanBuilder booleanBuilder = new BooleanBuilder();
		booleanBuilder.and(qUser.phone.any().number.eq("1234567890"));
		User myUser = userRepository.findAll(booleanBuilder).iterator().next();
		System.out.println(myUser);

		System.out.println("==========Phone==========");
		phoneRepository.findAll().forEach(System.out::println);
		System.out.println("==========Phones==========");
		QPhone qPhone = QPhone.phone;
		BooleanBuilder booleanBuilder2 = new BooleanBuilder();
		booleanBuilder2.and(qPhone.user.id.eq(1L)).and(qPhone.number.eq("1234567890"));
		Iterable<Phone> phones = phoneRepository.findAll(booleanBuilder2);

		phones.forEach(System.out::println);

	}

	@Test
	public void testPath() {
		User user = new User();
		user.setId(1L);
		user.setName("John Doe");

		Phone phone = new Phone();
		phone.setId(3l);

		PathBuilder quser =PredicateBuilder.pathBuilder(phone);
		// in a loop get propertiy value

		System.out.println( quser.get("id"));
		BooleanBuilder booleanBuilder = new BooleanBuilder();
		booleanBuilder.and(quser.get("id").eq(3L));
		Iterable<Phone> phones = phoneRepository.findAll(booleanBuilder);
		phones.forEach(System.out::println);
	}

	@Test
	public void testBuild_userRepo_queryUserName() throws IllegalAccessException, IntrospectionException, InvocationTargetException {
		User user = new User();
		//user.setId(1L);
		user.setName("Jane Doe2");
		Predicate p =PredicateBuilder.build(user);
		User myUser = userRepository.findAll(p).iterator().next();
		System.out.println(myUser);
		assert myUser.getName().equals("Jane Doe2");

		user.setId(myUser.getId());
		User myUser2 = userRepository.findAll(p).iterator().next();
		assert  myUser2.getId() == myUser.getId();

	}

	@Test
	public void testPhoneRepo() throws IllegalAccessException, IntrospectionException, InvocationTargetException {
		User user = new User();
		user.setId(1L);
		//user.setName("Jane Doe2");
		Phone phone = new Phone();
		phone.setPriority(1);
		//phone.setId(1L);
		phone.setUser(user);
		Predicate p =PredicateBuilder.build(phone);

		Phone phone1 = phoneRepository.findAll(p).iterator().next();
		System.out.println(phone1);
		assert phone1.getPriority() ==1l;

	}

	private void setup() {
		User user1 = new User();
		user1.setName("John Doe");
		User user2 = new User();
		user2.setName("Jane Doe2");

		var phone1 = new Phone();
		phone1.setNumber("1234567890");
		phone1.setUser(user1);
		phone1.setPriority(1);
		var phone2 = new Phone();
		phone2.setNumber("0987654321");
		phone2.setUser(user1);
		phone2.setPriority(2);
		var phone3 = new Phone();
		phone3.setNumber("1234567890");
		phone3.setUser(user2);
		phone3.setPriority(1);
		var phone4 = new Phone();
		phone4.setNumber("0987654321");
		phone4.setUser(user2);
		phone4.setPriority(2);

		user1.getPhone().addAll(Set.of(phone1, phone2));
		user2.getPhone().addAll(Set.of(phone3, phone4));

		userRepository.saveAll(Set.of(user1, user2));
		Iterable<User> users = userRepository.findAll();
		users.forEach(System.out::println);
	}

}
