package com.gonnect.quarkus.accounts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// DO NOT remove below lines
// Unfortunately, Apache Camel K is not able to detect all the dependencies declared in Maven pom.xml.
// Therefore, we will declare them inside the comment on the AccountRoute class. They must be preceded
// by a keyword camel-k as shown below.
// camel-k: dependency=mvn:org.apache.camel.quarkus:camel-quarkus-jackson
// camel-k: dependency=mvn:org.projectlombok:lombok:1.18.16
@ApplicationScoped
public class AccountRoute extends RouteBuilder {

    AccountService accountService = new AccountService();

    @Override
    public void configure() throws Exception {
        restConfiguration().bindingMode(RestBindingMode.json);

        rest("/accounts")
                .get("/{id}")
                .route().bean(accountService, "findById(${header.id})").endRest()
                .get("/customer/{customerId}")
                .route().bean(accountService, "findByCustomerId(${header.customerId})").endRest()
                .get().route().bean(accountService, "findAll").endRest()
                .post("/")
                .consumes("application/json").type(Account.class)
                .route().bean(accountService, "add(${body})").endRest();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public class Account {
        private Integer id;
        private String number;
        private int amount;
        private Integer customerId;
    }

    public class AccountService {

        private List<Account> accounts = new ArrayList<>();

        AccountService() {
            accounts.add(new Account(11, "838382828373", 60000, 1));
            accounts.add(new Account(22, "485858339387", 70000, 1));
            accounts.add(new Account(13, "093838383747", 90000, 2));
        }

        public Account findById(Integer id) {
            return accounts.stream()
                    .filter(it -> it.getId().equals(id))
                    .findFirst().get();
        }

        public List<Account> findByCustomerId(Integer customerId) {
            return accounts.stream()
                    .filter(it -> it.getCustomerId().equals(customerId))
                    .collect(Collectors.toList());
        }

        public List<Account> findAll() {
            return accounts;
        }

        public Account add(Account account) {
            account.setId(accounts.size() + 1);
            accounts.add(account);
            return account;
        }

    }
}
