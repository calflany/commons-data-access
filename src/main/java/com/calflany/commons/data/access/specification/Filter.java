package com.calflany.commons.data.access.specification;

import java.time.Instant;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Filter {

  private String field;

  private OperatorType operator;

  private String value;

  private Instant[] betweenDates;

  private List<String> values;// Used in case of IN operator

}