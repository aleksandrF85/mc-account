package com.example.mc_account.reposirory;

import com.example.mc_account.dto.filter.AccountSearchDto;
import com.example.mc_account.model.Account;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;
import java.util.List;

public interface AccountSpecification {

    static Specification<Account> withFilter(AccountSearchDto accountFilter) {
        return Specification.where(byAccountIds(accountFilter.getIds()))
                .and(buAuthor(accountFilter.getAuthor()))
                .and(byFirstName(accountFilter.getFirstName()))
                .and(byLastName(accountFilter.getLastName()))
                .and(byBirthDate(accountFilter.getBirthDateFrom(), accountFilter.getBirthDateTo()))
                .and(byCity(accountFilter.getCity()))
                .and(byCountry(accountFilter.getCountry()))
                .and(isBlocked(accountFilter.isBlocked()))
                .and(isDeleted(accountFilter.isDeleted()))
                .and(byAge(accountFilter.getAgeFrom(), accountFilter.getAgeTo()));
    }

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

    static Specification<Account> byBirthDate(OffsetDateTime birthDateFrom, OffsetDateTime birthDateTo) {
        return ((root, query, criteriaBuilder) -> {
            if (birthDateFrom == null && birthDateTo == null) {
                return null;
            }
            if (birthDateFrom == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("birthDate"), birthDateTo);
            }
            if (birthDateTo == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("birthDate"), birthDateFrom);
            }
            return criteriaBuilder.between(root.get("birthDate"), birthDateFrom, birthDateTo);
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

    static  Specification<Account>  byAccountIds(List<Long> ids) {

        return ((root, query, criteriaBuilder) -> {
            if (ids == null || ids.isEmpty()) {
                return null;
            }
            return root.get("id").in(ids);
        });
    }

    static Specification<Account> isDeleted(boolean deleted) {

        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("isDeleted"), deleted));
    }

    static Specification<Account> isBlocked(boolean blocked) {

        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("isBlocked"), blocked));
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

