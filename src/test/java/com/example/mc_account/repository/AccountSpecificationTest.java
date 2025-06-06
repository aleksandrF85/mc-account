package com.example.mc_account.repository;

import com.example.mc_account.dto.filter.AccountSearchDto;
import com.example.mc_account.model.Account;
import com.example.mc_account.model.StatusCode;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static com.example.mc_account.repository.AccountSpecification.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AccountSpecificationTest {

    private Root<Account> root;
    private CriteriaQuery<?> query;
    private CriteriaBuilder cb;

    @BeforeEach
    void setUp() {
        root = mock(Root.class);
        query = mock(CriteriaQuery.class);
        cb = mock(CriteriaBuilder.class);
    }

    @Test
    void testNotCurrentUser() {
        @SuppressWarnings("unchecked")
        Path<UUID> idPath = mock(Path.class);
        when(root.get("id")).thenReturn((Path) idPath);

        Predicate predicate = mock(Predicate.class);
        UUID testId = UUID.randomUUID();

        when(cb.notEqual(idPath, testId)).thenReturn(predicate);

        Specification<Account> spec = notCurrentUser(testId);
        assertEquals(predicate, spec.toPredicate(root, query, cb));

        // Проверяем null
        spec = notCurrentUser(null);
        assertNull(spec.toPredicate(root, query, cb));
    }

    @Test
    void testByAccountIds() {
        @SuppressWarnings("unchecked")
        Path<UUID> idPath = mock(Path.class);
        when(root.get("id")).thenReturn((Path) idPath);

        List<String> ids = List.of(UUID.randomUUID().toString());
        Specification<Account> spec = byAccountIds(ids);

        Predicate predicate = mock(Predicate.class);
        when(idPath.in(anyList())).thenReturn(predicate);

        assertEquals(predicate, spec.toPredicate(root, query, cb));

        // Пустой список -> всегда false (disjunction)
        spec = byAccountIds(List.of());
        Predicate disjunctionPredicate = mock(Predicate.class);
        when(cb.disjunction()).thenReturn(disjunctionPredicate);

        Predicate resultPredicate = spec.toPredicate(root, query, cb);
        assertNotNull(resultPredicate);
        assertEquals(disjunctionPredicate, resultPredicate);

        // null -> null
        spec = byAccountIds(null);
        assertNull(spec.toPredicate(root, query, cb));
    }


    @Test
    void testByAuthor() {
        when(root.get("firstName")).thenReturn(mock(Path.class));
        when(root.get("lastName")).thenReturn(mock(Path.class));

        Predicate predicate = mock(Predicate.class);
        when(cb.like(any(), anyString())).thenReturn(predicate);
        when(cb.or(any(), any())).thenReturn(predicate);

        Specification<Account> spec = byAuthor("John");
        assertEquals(predicate, spec.toPredicate(root, query, cb));

        spec = byAuthor(null);
        assertNull(spec.toPredicate(root, query, cb));

        spec = byAuthor("");
        assertNull(spec.toPredicate(root, query, cb));
    }

    @Test
    void testByFirstName() {
        when(root.get("firstName")).thenReturn(mock(Path.class));
        Predicate predicate = mock(Predicate.class);
        when(cb.like(any(), anyString())).thenReturn(predicate);

        Specification<Account> spec = byFirstName("John");
        assertEquals(predicate, spec.toPredicate(root, query, cb));

        spec = byFirstName(null);
        assertNull(spec.toPredicate(root, query, cb));

        spec = byFirstName("");
        assertNull(spec.toPredicate(root, query, cb));
    }

    @Test
    void testByLastName() {
        when(root.get("lastName")).thenReturn(mock(Path.class));
        Predicate predicate = mock(Predicate.class);
        when(cb.like(any(), anyString())).thenReturn(predicate);

        Specification<Account> spec = byLastName("Doe");
        assertEquals(predicate, spec.toPredicate(root, query, cb));

        spec = byLastName(null);
        assertNull(spec.toPredicate(root, query, cb));

        spec = byLastName("");
        assertNull(spec.toPredicate(root, query, cb));
    }

    @Test
    void testByCity() {
        @SuppressWarnings("unchecked")
        Path<String> cityPath = mock(Path.class);
        when(root.get("city")).thenReturn((Path) cityPath);

        Predicate predicate = mock(Predicate.class);
        when(cb.equal(cityPath, "New York")).thenReturn(predicate);

        Specification<Account> spec = byCity("New York");
        assertEquals(predicate, spec.toPredicate(root, query, cb));

        spec = byCity(null);
        assertNull(spec.toPredicate(root, query, cb));

        spec = byCity("");
        assertNull(spec.toPredicate(root, query, cb));
    }

    @Test
    void testByCountry() {
        @SuppressWarnings("unchecked")
        Path<String> countryPath = mock(Path.class);
        when(root.get("country")).thenReturn((Path) countryPath);

        Predicate predicate = mock(Predicate.class);
        when(cb.equal(countryPath, "USA")).thenReturn(predicate);

        Specification<Account> spec = byCountry("USA");
        assertEquals(predicate, spec.toPredicate(root, query, cb));

        spec = byCountry(null);
        assertNull(spec.toPredicate(root, query, cb));

        spec = byCountry("");
        assertNull(spec.toPredicate(root, query, cb));
    }

    @Test
    void testByStatusCode() {
        @SuppressWarnings("unchecked")
        Path<StatusCode> statusCodePath = mock(Path.class);
        when(root.get("statusCode")).thenReturn((Path) statusCodePath);

        Predicate predicate = mock(Predicate.class);
        StatusCode code = StatusCode.NONE; // пример enum

        when(cb.equal(statusCodePath, code)).thenReturn(predicate);

        Specification<Account> spec = byStatusCode(code);
        assertEquals(predicate, spec.toPredicate(root, query, cb));

        spec = byStatusCode(null);
        assertNull(spec.toPredicate(root, query, cb));
    }

    @Test
    void testIsDeleted() {
        @SuppressWarnings("unchecked")
        Path<Boolean> deletedPath = mock(Path.class);
        when(root.get("deleted")).thenReturn((Path) deletedPath);

        Predicate predicate = mock(Predicate.class);
        when(cb.equal(deletedPath, true)).thenReturn(predicate);

        Specification<Account> spec = isDeleted(true);
        assertEquals(predicate, spec.toPredicate(root, query, cb));
    }

    @Test
    void testByAge() {
        @SuppressWarnings("unchecked")
        Path<OffsetDateTime> birthDatePath = mock(Path.class);
        when(root.get("birthDate")).thenReturn((Path) birthDatePath);

        Predicate predicate = mock(Predicate.class);

        // ageFrom and ageTo null or <= 0 => returns null
        Specification<Account> spec = byAge(null, null);
        assertNull(spec.toPredicate(root, query, cb));

        spec = byAge(-1, 0);
        assertNull(spec.toPredicate(root, query, cb));

        // only ageFrom
        when(cb.lessThanOrEqualTo(eq(birthDatePath), any(OffsetDateTime.class))).thenReturn(predicate);
        spec = byAge(30, null);
        assertEquals(predicate, spec.toPredicate(root, query, cb));

        // only ageTo
        when(cb.greaterThanOrEqualTo(eq(birthDatePath), any(OffsetDateTime.class))).thenReturn(predicate);
        spec = byAge(null, 40);
        assertEquals(predicate, spec.toPredicate(root, query, cb));

        // both ageFrom and ageTo
        when(cb.between(eq(birthDatePath), any(OffsetDateTime.class), any(OffsetDateTime.class))).thenReturn(predicate);
        spec = byAge(20, 40);
        assertEquals(predicate, spec.toPredicate(root, query, cb));
    }

    @Test
    void testWithFilter_combinesAll() {
        AccountSearchDto filter = new AccountSearchDto();
        filter.setIds(List.of(UUID.randomUUID().toString()));
        filter.setAuthor("author");
        filter.setFirstName("John");
        filter.setLastName("Doe");
        filter.setCity("City");
        filter.setCountry("Country");
        filter.setDeleted(false);
        filter.setAgeFrom(20);
        filter.setAgeTo(40);
        filter.setCurrentUserId(UUID.randomUUID());

        Specification<Account> spec = withFilter(filter);
        assertNotNull(spec); // Проверяем, что не null и создаётся успешно
    }
}
