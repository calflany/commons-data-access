package com.calflany.commons.data.access;

import static org.springframework.data.jpa.domain.Specification.where;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.calflany.commons.data.access.repository.EntityRepository;
import com.calflany.commons.data.access.specification.Filter;
import com.calflany.commons.data.access.specification.OperatorType;

import jakarta.persistence.EntityNotFoundException;

public abstract class AbstractDAO<E, R extends EntityRepository<E, Long>> implements DAO<E, R> {

    @Autowired
    protected R repository;

    @Override
    public E findById(long id) {
        return this.repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Entity not found: " + id));
    }

    @Override
    public List<E> findAll() {
        return this.repository.findAll();
    }

    @Override
    public Page<E> findAll(Pageable pageable) {
        return this.repository.findAll(pageable);
    }

    @Override
    public Long count() {
        return this.repository.count();
    }

    @Override
    public List<E> getQueryResult(Filter... filters) {
        return this.repository
                .findAll(getSpecificationFromFilters(
                        Arrays.stream(filters).collect(Collectors.toCollection(ArrayList::new))));
    }

    @Override
    public Page<E> getQueryResult(Pageable pageable, Filter... filters) {
        return this.repository
                .findAll(getSpecificationFromFilters(
                        Arrays.stream(filters).collect(Collectors.toCollection(ArrayList::new))), pageable);
    }

    @Override
    public boolean existsById(Long id) {
        return this.repository.existsById(id);
    }

    @Override
    public E save(E entity) {
        return this.repository.save(entity);
    }

    @Override
    public R getRepository() {
        return this.repository;
    }

    private Specification<E> getSpecificationFromFilters(List<Filter> filters) {
        Specification<E> specification = where(createSpecification(filters.remove(0)));
        filters.forEach(filter -> specification.and(createSpecification(filter)));
        return specification;
    }

    private Specification<E> createSpecification(Filter filter) {

        if (filter.getValue() == null && filter.getValues() == null && filter.getBetweenDates() == null) {
            return null;
        }

        switch (filter.getOperator()) {

            case AFTER:
                this.validateInputDateType(filter.getOperator(), filter.getValue());
                return (root, query, cb) -> cb.greaterThan(root.get(filter.getField()),
                        (Instant) castToRequiredType(root.get(filter.getField()).getJavaType(),
                                filter.getValue()));

            case AFTER_OR_EQUAL:
                this.validateInputDateType(filter.getOperator(), filter.getValue());
                return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get(filter.getField()),
                        (Instant) castToRequiredType(root.get(filter.getField()).getJavaType(),
                                filter.getValue()));

            case BEFORE:
                this.validateInputDateType(filter.getOperator(), filter.getValue());
                return (root, query, cb) -> cb.lessThan(root.get(filter.getField()),
                        (Instant) castToRequiredType(root.get(filter.getField()).getJavaType(),
                                filter.getValue()));

            case BEFORE_OR_EQUAL:
                this.validateInputDateType(filter.getOperator(), filter.getValue());
                return (root, query, cb) -> cb.lessThanOrEqualTo(root.get(filter.getField()),
                        (Instant) castToRequiredType(root.get(filter.getField()).getJavaType(),
                                filter.getValue()));

            case BETWEEN:
                return (root, query, cb) -> cb.between(root.get(filter.getField()), filter.getBetweenDates()[0],
                        filter.getBetweenDates()[1]);

            case EQUALS:
                return (root, query, cb) -> cb.equal(root.get(filter.getField()),
                        castToRequiredType(root.get(filter.getField()).getJavaType(),
                                filter.getValue()));

            case NOT_EQUALS:
                return (root, query, cb) -> cb.notEqual(root.get(filter.getField()),
                        castToRequiredType(root.get(filter.getField()).getJavaType(),
                                filter.getValue()));

            case GREATER_THAN:
                return (root, query, cb) -> cb.gt(root.get(filter.getField()),
                        (Number) castToRequiredType(root.get(filter.getField()).getJavaType(), filter.getValue()));

            case LESS_THAN:
                return (root, query, cb) -> cb.lt(root.get(filter.getField()),
                        (Number) castToRequiredType(
                                root.get(filter.getField()).getJavaType(),
                                filter.getValue()));

            case GREATER_THAN_OR_EQUAL_TO:
                this.validateInputNumberType(filter.getOperator(), filter.getValue());
                return (root, query, cb) -> cb.or(
                        cb.gt(root.get(filter.getField()),
                                (Number) castToRequiredType(root.get(filter.getField()).getJavaType(),
                                        filter.getValue())),
                        cb.equal(root.get(filter.getField()),
                                castToRequiredType(root.get(filter.getField()).getJavaType(),
                                        filter.getValue())));

            case LESS_THAN_OR_EQUAL_TO:
                this.validateInputNumberType(filter.getOperator(), filter.getValue());
                return (root, query, cb) -> cb.or(
                        cb.lt(root.get(filter.getField()),
                                (Number) castToRequiredType(
                                        root.get(filter.getField()).getJavaType(), filter.getValue())),
                        cb.equal(root.get(filter.getField()),
                                castToRequiredType(root.get(filter.getField()).getJavaType(),
                                        filter.getValue())));

            case LIKE:
                return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(filter.getField()),
                        "%" + filter.getValue() + "%");

            case IN:
                return (root, query, criteriaBuilder) -> criteriaBuilder.in(root.get(filter.getField()))
                        .value(castToRequiredType(
                                root.get(filter.getField()).getJavaType(),
                                filter.getValues()));

            default:
                throw new RuntimeException("Operation not supported.");

        }

    }

    private Object castToRequiredType(Class fieldType, String value) {
        if (fieldType.isAssignableFrom(Double.class)) {
            return Double.valueOf(value);
        } else if (fieldType.isAssignableFrom(Integer.class)) {
            return Integer.valueOf(value);
        } else if (Enum.class.isAssignableFrom(fieldType)) {
            return Enum.valueOf(fieldType, value);
        }
        return null;
    }

    private Object castToRequiredType(Class fieldType, List<String> value) {
        List<Object> lists = new ArrayList<>();
        for (String s : value) {
            lists.add(castToRequiredType(fieldType, s));
        }
        return lists;
    }

    private void validateInputDateType(OperatorType type, Object value) {

        if ((OperatorType.AFTER == type || OperatorType.BEFORE == type || OperatorType.BETWEEN == type
                || OperatorType.AFTER_OR_EQUAL == type || OperatorType.BEFORE_OR_EQUAL == type)
                && (!(Instant.class.isAssignableFrom(value.getClass())))) {
            throw new IllegalArgumentException(
                    "Invalid type.  Expected Instant but was " + value.getClass().getSimpleName());
        }
    }

    private void validateInputNumberType(OperatorType type, Object value) {

        if ((OperatorType.LESS_THAN_OR_EQUAL_TO == type || OperatorType.GREATER_THAN_OR_EQUAL_TO == type
                || OperatorType.LESS_THAN == type || OperatorType.GREATER_THAN == type)
                && (!(Number.class.isAssignableFrom(value.getClass())))) {
            throw new IllegalArgumentException(
                    "Invalid type.  Expected instance of type Number, but was " + value.getClass().getSimpleName());
        }

    }

}