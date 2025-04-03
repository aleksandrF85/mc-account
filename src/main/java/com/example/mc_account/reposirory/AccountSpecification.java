package com.example.mc_account.reposirory;

import com.example.mc_account.dto.filter.AccountSearchDto;
import com.example.mc_account.model.Account;
import com.example.mc_account.model.StatusCode;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;
import java.util.List;

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

    static Specification<Account> byAccountIds(List<Long> ids) {

        return ((root, query, criteriaBuilder) -> {
            if (ids == null || ids.isEmpty()) {
                return null;
            }
            return root.get("id").in(ids);
        });
    }

    //TODO уточнить параметры поиска по Автору
    static Specification<Account> buAuthor(String author) {
        return ((root, query, criteriaBuilder) -> {
            if (author == null || author.isEmpty()) {
                return null;
            }
            return criteriaBuilder.equal(root.get("author"), author);
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
                criteriaBuilder.equal(root.get("isDeleted"), deleted));
    }

    static Specification<Account> byAge(Integer ageFrom, Integer ageTo) {

        OffsetDateTime birthdayFrom = OffsetDateTime.now().minusYears(ageFrom);
        OffsetDateTime birthdayTo = OffsetDateTime.now().minusYears(ageTo);

        return ((root, query, criteriaBuilder) -> {

            if (ageFrom == null && ageTo == null) {
                return null;
            }
            if (ageFrom == null || ageFrom <= 0) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("birthDate"), birthdayTo);
            }
            if (ageTo == null || ageTo <= 0) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("birthDate"), birthdayFrom);
            }
            return criteriaBuilder.between(root.get("birthDate"), birthdayFrom, birthdayTo);
        });
    }
}

