package com.example.mc_account.repository;

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
                .and(byAuthor(accountFilter.getAuthor()))
                .and(byFirstName(accountFilter.getFirstName()))
                .and(byLastName(accountFilter.getLastName()))
                .and(byCity(accountFilter.getCity()))
                .and(byCountry(accountFilter.getCountry()))
                .and(isDeleted(accountFilter.isDeleted()))
                .and(byAge(accountFilter.getAgeFrom(), accountFilter.getAgeTo()))
                .and(notCurrentUser(accountFilter.getCurrentUserId()));
    }

    static Specification<Account> notCurrentUser(UUID id) {
        return (root, query, cb) -> {
            if (id == null) return null; // игнорируем фильтрацию по id, если не передан параметр

            return cb.notEqual(root.get("id"), id);
        };
    }

    static Specification<Account> byAccountIds(List<String> ids) {
        return (root, query, cb) -> {
            if (ids == null) return null; // игнорируем фильтрацию по id, если не передан параметр
            if (ids.isEmpty()) return cb.disjunction(); // => WHERE false (возвращает пустой результат)
            List<UUID> uuidList = ids.stream().map(UUID::fromString).toList();
            return root.get("id").in(uuidList);
        };
    }

    static Specification<Account> byAuthor(String author) {
        return (root, query, criteriaBuilder) -> {
            if (author == null || author.isEmpty()) {
                return null;
            }
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + author.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + author.toLowerCase() + "%")
            );
        };
    }

    static Specification<Account> byFirstName(String firstName) {
        return ((root, query, criteriaBuilder) -> {
            if (firstName == null || firstName.isEmpty()) {
                return null;
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
        });
    }

    static Specification<Account> byLastName(String lastName) {
        return ((root, query, criteriaBuilder) -> {
            if (lastName == null || lastName.isEmpty()) {
                return null;
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
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
    }

    static Specification<Account> byAge(Integer ageFrom, Integer ageTo) {
        return (root, query, cb) -> {
            OffsetDateTime now = OffsetDateTime.now();

            if ((ageFrom == null || ageFrom <= 0) && (ageTo == null || ageTo <= 0)) {
                return null;
            }

            if (ageFrom != null && ageFrom > 0 && (ageTo == null || ageTo <= 0)) {
                return cb.lessThanOrEqualTo(root.get("birthDate"), now.minusYears(ageFrom));
            }

            if (ageTo != null && ageTo > 0 && (ageFrom == null || ageFrom <= 0)) {
                return cb.greaterThanOrEqualTo(root.get("birthDate"), now.minusYears(ageTo));
            }

            OffsetDateTime fromDate = now.minusYears(ageTo);
            OffsetDateTime toDate = now.minusYears(ageFrom);

            return cb.between(root.get("birthDate"), fromDate, toDate);
        };
    }
}

