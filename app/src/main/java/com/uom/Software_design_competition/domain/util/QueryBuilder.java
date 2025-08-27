package com.uom.Software_design_competition.domain.util;

import com.uom.Software_design_competition.application.transport.request.FilterValue;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class QueryBuilder {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_ZONED_DATE_TIME;

    public <T> Specification<T> buildTransformerSpecification(List<FilterValue> filterValues) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            for (FilterValue filterValue : filterValues) {
                Object[] values = filterValue.getValue();
                String columnName = filterValue.getColumnName();
                String operation = filterValue.getOperation();

                Predicate predicate = buildPredicate(root, criteriaBuilder, columnName, operation, values);
                if (predicate != null) {
                    predicates.add(predicate);
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public <T> Specification<T> buildInspectionSpecification(List<FilterValue> filterValues) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            for (FilterValue filterValue : filterValues) {
                Object[] values = filterValue.getValue();
                String columnName = filterValue.getColumnName();
                String operation = filterValue.getOperation();

                Predicate predicate = buildPredicate(root, criteriaBuilder, columnName, operation, values);
                if (predicate != null) {
                    predicates.add(predicate);
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private <T> Predicate buildPredicate(Root<T> root, CriteriaBuilder cb, String columnName, String operation, Object[] values) {
        Path<Object> fieldPath = root.get(columnName);

        switch (operation.toUpperCase()) {
            case "INCLUDE":
                return buildIncludePredicate(cb, fieldPath, values);

            case "EQUAL":
                if (values.length > 0) {
                    if (values[0] instanceof String && isParsableZonedDateTime((String) values[0])) {
                        List<LocalDateTime> dateList = parseDateList(values);
                        return fieldPath.in(dateList);
                    } else if (values[0] instanceof String) {
                        // For string equality with case-insensitive matching
                        List<Predicate> equalPredicates = new ArrayList<>();
                        for (Object value : values) {
                            equalPredicates.add(cb.like(cb.lower(fieldPath.as(String.class)),
                                    ((String) value).toLowerCase()));
                        }
                        return cb.or(equalPredicates.toArray(new Predicate[0]));
                    } else {
                        return fieldPath.in(Arrays.asList(values));
                    }
                }
                break;

            case "IS_EMPTY":
            case "IS EMPTY":
                return cb.or(
                        cb.isNull(fieldPath),
                        cb.equal(cb.trim(fieldPath.as(String.class)), "")
                );

            case "GREATER_THAN":
            case "GREATER THAN":
                return cb.greaterThan(fieldPath.as(Comparable.class),
                        (Comparable) adjustForGreaterThan(values[0]));

            case "GREATER_THAN_OR_EQUAL":
            case "GREATER THAN OR EQUAL":
                return cb.greaterThanOrEqualTo(fieldPath.as(Comparable.class),
                        (Comparable) parseIfDate(values[0]));

            case "LESS_THAN":
            case "LESS THAN":
                return cb.lessThan(fieldPath.as(Comparable.class),
                        (Comparable) adjustForLessThan(values[0]));

            case "LESS_THAN_OR_EQUAL":
            case "LESS THAN OR EQUAL":
                return cb.lessThanOrEqualTo(fieldPath.as(Comparable.class),
                        (Comparable) adjustForLessThanOrEqual(values[0]));

            case "BETWEEN":
                if (values.length == 2) {
                    return cb.between(fieldPath.as(Comparable.class),
                            (Comparable) parseIfDate(values[0]),
                            (Comparable) parseIfDate(values[1]));
                }
                break;

            case "LAST 30 MINUTES":
                LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(30);
                return cb.greaterThanOrEqualTo(fieldPath.as(LocalDateTime.class), thirtyMinutesAgo);

            case "LAST DAY":
                LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
                return cb.greaterThanOrEqualTo(fieldPath.as(LocalDateTime.class), oneDayAgo);

            case "LAST WEEK":
                LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
                return cb.greaterThanOrEqualTo(fieldPath.as(LocalDateTime.class), oneWeekAgo);

            case "LAST YEAR":
                LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
                return cb.greaterThanOrEqualTo(fieldPath.as(LocalDateTime.class), oneYearAgo);

            default:
                return null;
        }
        return null;
    }

    private Predicate buildIncludePredicate(CriteriaBuilder cb, Path<Object> fieldPath, Object[] values) {
        Object firstValue = values[0];

        if (firstValue instanceof String && isParsableZonedDateTime((String) firstValue)) {
            LocalDateTime startOfDay = parseIfDate(firstValue);
            LocalDateTime startOfNextDay = startOfDay.plusDays(1);

            return cb.and(
                    cb.greaterThanOrEqualTo(fieldPath.as(LocalDateTime.class), startOfDay),
                    cb.lessThan(fieldPath.as(LocalDateTime.class), startOfNextDay)
            );
        } else if (firstValue instanceof String) {
            return cb.like(cb.lower(fieldPath.as(String.class)),
                    "%" + ((String) firstValue).toLowerCase() + "%");
        } else {
            return fieldPath.in(Arrays.asList(values));
        }
    }

    private static boolean isParsableZonedDateTime(String input) {
        try {
            ZonedDateTime.parse(input, DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private static LocalDateTime parseIfDate(Object input) {
        if (input instanceof String && isParsableZonedDateTime((String) input)) {
            return ZonedDateTime.parse((String) input, DATE_FORMATTER).toLocalDateTime();
        }
        if (input instanceof LocalDateTime) {
            return (LocalDateTime) input;
        }
        return (LocalDateTime) input; // Assume it's already the correct type
    }

    private static List<LocalDateTime> parseDateList(Object[] values) {
        List<LocalDateTime> dates = new ArrayList<>();
        for (Object val : values) {
            if (val instanceof String && isParsableZonedDateTime((String) val)) {
                dates.add(ZonedDateTime.parse((String) val, DATE_FORMATTER).toLocalDateTime());
            } else if (val instanceof LocalDateTime) {
                dates.add((LocalDateTime) val);
            }
        }
        return dates;
    }

    private static LocalDateTime adjustForGreaterThan(Object input) {
        if (input instanceof String && isParsableZonedDateTime((String) input)) {
            ZonedDateTime dateTime = ZonedDateTime.parse((String) input, DATE_FORMATTER);
            LocalDateTime localDateTime = dateTime.toLocalDateTime();

            // If exactly midnight, shift to next day
            if (localDateTime.toLocalTime().equals(localDateTime.toLocalDate().atStartOfDay().toLocalTime())) {
                localDateTime = localDateTime.plusDays(1).toLocalDate().atStartOfDay();
            }
            return localDateTime;
        }
        return (LocalDateTime) input;
    }

    private static LocalDateTime adjustForLessThan(Object input) {
        if (input instanceof String && isParsableZonedDateTime((String) input)) {
            ZonedDateTime dateTime = ZonedDateTime.parse((String) input, DATE_FORMATTER);
            return dateTime.toLocalDateTime();
        }
        return (LocalDateTime) input;
    }

    private static LocalDateTime adjustForLessThanOrEqual(Object input) {
        if (input instanceof String && isParsableZonedDateTime((String) input)) {
            ZonedDateTime dateTime = ZonedDateTime.parse((String) input, DATE_FORMATTER);
            LocalDateTime localDateTime = dateTime.toLocalDateTime();
            // Shift to start of the next day
            localDateTime = localDateTime.plusDays(1).toLocalDate().atStartOfDay();
            return localDateTime;
        }
        return (LocalDateTime) input;
    }
}