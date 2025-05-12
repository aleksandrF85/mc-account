package com.example.mc_account.reposirory;

import com.example.mc_account.dto.filter.AccountSearchDto;
import com.example.mc_account.model.Account;
import com.example.mc_account.model.StatusCode;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface AccountSpecification {

    static Specification<Account> withFilter(AccountSearchDto accountFilter) {
        return Specification.where(byAccountIds(accountFilter.getIds()))
                .and(buAuthor(accountFilter.getAuthor()))
                .and(byFirstName(accountFilter.getFirstName()))
                .and(byLastName(accountFilter.getLastName()))
                .and(byCity(accountFilter.getCity()))
                .and(byCountry(accountFilter.getCountry()))
                .and(byStatusCode(accountFilter.getStatusCode()))
                .and(isDeleted(accountFilter.isDeleted()))
                .and(byAge(accountFilter.getAgeFrom(), accountFilter.getAgeTo()));
    }

    static Specification<Account> byAccountIds(List<String> ids) {

        return ((root, query, criteriaBuilder) -> {
            if (ids == null || ids.isEmpty()) {
                return null;
            }

            List<UUID> uuidList = ids.stream().map(UUID::fromString).toList();

            return root.get("id").in(uuidList);
        });
    }

    //TODO уточнить параметры поиска по Автору
    static Specification<Account> buAuthor(String author) {
        return ((root, query, criteriaBuilder) -> {
            if (author == null || author.isEmpty()) {
                return null;
            }
            return criteriaBuilder.equal(root.get("firstName"), author);
        });
    }

    static Specification<Account> byFirstName(String firstName) {
        return ((root, query, criteriaBuilder) -> {
            if (firstName == null || firstName.isEmpty()) {
                return null;
            }
            return criteriaBuilder.equal(root.get("firstName"), firstName);
        });
    }

    static Specification<Account> byLastName(String lastName) {
        return ((root, query, criteriaBuilder) -> {
            if (lastName == null || lastName.isEmpty()) {
                return null;
            }
            return criteriaBuilder.equal(root.get("lastName"), lastName);
        });
    }

    static Specification<Account> byCity(String city) {
        return ((root, query, criteriaBuilder) -> {
            if (city == null || city.isEmpty()) {
                return null;
            }
            return criteriaBuilder.equal(root.get("city"), city);
        });
    }

    static Specification<Account> byCountry(String country) {
        return ((root, query, criteriaBuilder) -> {
            if (country == null || country.isEmpty()) {
                return null;
            }
            return criteriaBuilder.equal(root.get("country"), country);
        });
    }

    static Specification<Account> byStatusCode(StatusCode statusCode) {
        return ((root, query, criteriaBuilder) -> {
            if (statusCode == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("statusCode"), statusCode);
        });
    }

    static Specification<Account> isDeleted(boolean deleted) {

        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("deleted"), deleted));
//        return ((root, query, criteriaBuilder) ->
//        {
//            if (deleted){
//                return criteriaBuilder.isTrue(root.get("deleted"));
//            }
//            return criteriaBuilder.isFalse(root.get("deleted"));
//        });
    }

    static Specification<Account> byAge(Integer ageFrom, Integer ageTo) {


        return ((root, query, criteriaBuilder) -> {

            if (ageFrom == null && ageTo == null || ageFrom == 0 && ageTo == 0) {
                return null;
            }

//            OffsetDateTime birthdayFrom = OffsetDateTime.now().minusYears(ageFrom);
//            OffsetDateTime birthdayTo = OffsetDateTime.now().minusYears(ageTo);

            if (ageFrom == null || ageFrom <= 0) {

                return criteriaBuilder.lessThanOrEqualTo(root.get("birthDate"), OffsetDateTime.now().minusYears(ageTo));
            }
            if (ageTo == null || ageTo <= 0) {

                return criteriaBuilder.greaterThanOrEqualTo(root.get("birthDate"), OffsetDateTime.now().minusYears(ageFrom));
            }
            return criteriaBuilder.between(root.get("birthDate"), OffsetDateTime.now().minusYears(ageFrom), OffsetDateTime.now().minusYears(ageTo));
        });
    }
}

