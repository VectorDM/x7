/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package x7.core.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import x7.core.bean.Criteria.Fetch;
import x7.core.bean.Criteria.X;
import x7.core.repository.Mapped;
import x7.core.util.BeanMapUtil;
import x7.core.util.BeanUtil;
import x7.core.util.BeanUtilX;
import x7.core.util.NumberUtil;
import x7.core.util.StringUtil;
import x7.core.web.Fetched;
import x7.core.web.Paged;

/**
 * Standard Query Builder
 * 
 * @author Sim
 *
 */
public class CriteriaBuilder {

	public final static String SPACE = " ";

	private Criteria criteria;

	private CriteriaBuilder instance;

	private P p = new P() {

		private X x = null;

		@Override
		public void under(X x) {
			this.x = x;
		}

		@Override
		public CriteriaBuilder eq(String property, Object value) {

			if (value == null)
				return instance;

			if (isBaseType_0(property, value))
				return instance;
			if (isNullOrEmpty(value))
				return instance;

			x.setPredicate(Predicate.EQ);
			x.setKey(property);
			x.setValue(value);

			return instance;
		}

		@Override
		public CriteriaBuilder lt(String property, Object value) {

			if (value == null)
				return instance;

			if (isBaseType_0(property, value))
				return instance;
			if (isNullOrEmpty(value))
				return instance;

			x.setPredicate(Predicate.LT);
			x.setKey(property);
			x.setValue(value);

			return instance;
		}

		@Override
		public CriteriaBuilder lte(String property, Object value) {

			if (value == null)
				return instance;

			if (isBaseType_0(property, value))
				return instance;
			if (isNullOrEmpty(value))
				return instance;

			x.setPredicate(Predicate.LTE);
			x.setKey(property);
			x.setValue(value);

			return instance;
		}

		@Override
		public CriteriaBuilder gt(String property, Object value) {

			check(property);

			if (value == null)
				return instance;
			if (isBaseType_0(property, value))
				return instance;
			if (isNullOrEmpty(value))
				return instance;

			x.setPredicate(Predicate.GT);
			x.setKey(property);
			x.setValue(value);

			return instance;
		}

		@Override
		public CriteriaBuilder gte(String property, Object value) {

			if (value == null)
				return instance;

			if (isBaseType_0(property, value))
				return instance;
			if (isNullOrEmpty(value))
				return instance;

			x.setPredicate(Predicate.GTE);
			x.setKey(property);
			x.setValue(value);

			return instance;
		}

		@Override
		public CriteriaBuilder not(String property, Object value) {

			if (value == null)
				return instance;

			if (isBaseType_0(property, value))
				return instance;
			if (isNullOrEmpty(value))
				return instance;

			x.setPredicate(Predicate.NOT);
			x.setKey(property);
			x.setValue(value);

			return instance;
		}

		@Override
		public CriteriaBuilder like(String property, Object value) {

			check(property);

			if (value == null)
				return instance;

			if (isNullOrEmpty(value))
				return instance;

			x.setPredicate(Predicate.LIKE);
			x.setKey(property);
			x.setValue("%" + value + "%");

			return instance;
		}

		@Override
		public CriteriaBuilder between(String property, Object min, Object max) {

			if (min == null || max == null)
				return instance;

			if (isBaseType_0(property, max))
				return instance;
			if (isNullOrEmpty(max))
				return instance;
			if (isNullOrEmpty(min))
				return instance;

			MinMax minMax = new MinMax();
			minMax.setMin(min);
			minMax.setMax(max);

			x.setPredicate(Predicate.BETWEEN);
			x.setKey(property);
			x.setValue(minMax);

			return instance;
		}

		@Override
		public CriteriaBuilder in(String property, List<Object> list) {

			check(property);

			if (list == null || list.isEmpty())
				return instance;

			List<Object> tempList = new ArrayList<Object>();
			for (Object obj : list) {
				if (Objects.isNull(obj))
					continue;
				if (!tempList.contains(obj)) {
					tempList.add(obj);
				}
			}

			if (list.isEmpty())
				return instance;

			x.setPredicate(Predicate.IN);
			x.setKey(property);
			x.setValue(tempList);

			return instance;
		}

		@Override
		public CriteriaBuilder notIn(String property, List<Object> list) {

			check(property);

			if (list == null || list.isEmpty())
				return instance;

			List<Object> tempList = new ArrayList<Object>();
			for (Object obj : list) {
				if (Objects.isNull(obj))
					continue;
				if (!tempList.contains(obj)) {
					tempList.add(obj);
				}
			}

			if (list.isEmpty())
				return instance;

			x.setPredicate(Predicate.NOT_IN);
			x.setKey(property);
			x.setValue(tempList);

			return instance;
		}

		@Override
		public P x() {

			X xx = new X();

			x.setPredicate(Predicate.X);

			x.setValue(xx);

			p.under(xx);

			return p;
		}
	};

	private CriteriaBuilder() {
		this.instance = this;
	}

	private CriteriaBuilder(Criteria criteria) {
		this.criteria = criteria;
		this.instance = this;
	}

	public static CriteriaBuilder build(Class<?> clz) {
		Criteria criteria = new Criteria();
		criteria.setClz(clz);
		CriteriaBuilder builder = new CriteriaBuilder(criteria);

		if (criteria.getParsed() == null) {
			Parsed parsed = Parser.get(clz);
			criteria.setParsed(parsed);
		}

		return builder;
	}

	public static CriteriaBuilder build(Class<?> clz, Paged paged) {
		Criteria criteria = new Criteria();
		criteria.setClz(clz);
		CriteriaBuilder builder = new CriteriaBuilder(criteria);

		if (criteria.getParsed() == null) {
			Parsed parsed = Parser.get(clz);
			criteria.setParsed(parsed);
		}

		if (paged != null) {
			builder.paged(paged);
		}

		return builder;
	}

	public static Fetchable buildFetchable(Class<?> clz, Fetched ro) {
		CriteriaBuilder b = new CriteriaBuilder();
		Fetchable builder = b.new Fetchable(clz, ro);

		if (ro != null) {

			if (ro instanceof Paged) {
				builder.paged((Paged) ro);
			}
		}

		return builder;
	}


	protected String getAliasPoint(String property) {
		return property.replace("->", ".");
	}

	public P and() {

		X x = new X();
		x.setConjunction(Conjunction.AND);

		this.criteria.add(x);
		p.under(x);

		return p;
	}

	public P or() {

		X x = new X();
		x.setConjunction(Conjunction.OR);

		this.criteria.add(x);
		p.under(x);

		return p;
	}

	public void paged(Paged paged) {

		criteria.paged(paged);

	}

	protected String getX(String xExpression) {
		if (xExpression.contains("(") || xExpression.contains(")") || xExpression.contains(" ")
				|| xExpression.contains("%"))
			throw new RuntimeException("unknow X-expression for x7 repository sql: " + xExpression);
		xExpression = xExpression.replace("[", "(");
		xExpression = xExpression.replace("]", ")");
		xExpression = xExpression.replace(".", " ");
		xExpression = xExpression.replace("->", ".");

		return xExpression;
	}

	public Class<?> getClz() {
		return this.criteria.getClz();
	}


	public static String[] parse(Criteria criteria) {

		StringBuilder sb = new StringBuilder();

		/*
		 * select column
		 */
		select(sb, criteria);

		/*
		 * from table
		 */
		boolean hasSourceScript = criteria.sourceScript(sb);

		/*
		 * StringList
		 */
		X groupBy = x(sb, criteria);


		/*
		 * sort
		 */
		sort(sb, criteria);

		String sql = sb.toString();

		String column = criteria.resultAllScript();

		String[] sqlArr = new String[3];
		String str = sql.replace(Mapped.TAG, column);
		sqlArr[1] = str;
		if (groupBy != null) {
			str = str.replaceAll(" +", " ");
			str = str.replace(") count", ") _count").replace(")count", ") _count");
			str = str.replace("count (", "count(");
			str = str.replace(" count ", " _count ");
			sqlArr[0] = "select count(tc." + groupBy.getKey() + ") count from (" + str + ") tc";
		} else {
			sqlArr[0] = sql.replace(Mapped.TAG, "COUNT(*) count");
		}
		sqlArr[2] = sql;

		if (hasSourceScript) {
			// sqlArr[1]: core sql
			Map<String, List<String>> map = new HashMap<>();
			{
				String[] arr = sqlArr[1].split(" ");
				for (String ele : arr) {
					if (ele.contains(".")) {
						ele = ele.replace(",", "");
						ele = ele.trim();
						String[] tc = ele.split("\\.");
						List<String> list = map.get(tc[0]);
						if (list == null) {
							list = new ArrayList<>();
							map.put(tc[0], list);
						}
						list.add(tc[1]);
					}
				}
			}
			FetchMapper fetchMapper = new FetchMapper();
			criteria.setFetchMapper(fetchMapper);
			Map<String, String> clzTableMapper = new HashMap<String, String>();
			{
				Set<Entry<String, List<String>>> set = map.entrySet();
				for (Entry<String, List<String>> entry : set) {
					String key = entry.getKey();
					List<String> list = entry.getValue();
					Parsed parsed = Parser.get(key);
					if (Objects.isNull(parsed))
						throw new RuntimeException("Entity Bean Not Exist: " + BeanUtil.getByFirstUpper(key));
					String tableName = parsed.getTableName();
					clzTableMapper.put(key, tableName);// clzName, tableName
					for (String property : list) {
						String mapper = parsed.getMapper(property);
						if (StringUtil.isNullOrEmpty(mapper)) {
							mapper = property;// dynamic
						}
						fetchMapper.put(key + "." + property, tableName + "." + mapper);
					}
				}
			}
			System.out.println(fetchMapper);
			for (int i = 0; i < 3; i++) {
				String temp = sqlArr[i];
				for (String property : fetchMapper.getPropertyMapperMap().keySet()) {
					temp = temp.replace(property, fetchMapper.mapper(property));
				}
				for (String clzName : clzTableMapper.keySet()) {
					String tableName = clzTableMapper.get(clzName);
					temp = BeanUtilX.mapperName(temp, clzName, tableName);
				}
				sqlArr[i] = temp;
			}

		} else {
			Parsed parsed = Parser.get(criteria.getClz());
			for (int i = 0; i < 3; i++) {
				sqlArr[i] = BeanUtilX.mapper(sqlArr[i], parsed);
			}
		}

		System.out.println(sqlArr[1]);

		return sqlArr;
	}

	private static void select(StringBuilder sb, Criteria criteria) {
		sb.append("SELECT").append(SPACE).append(Mapped.TAG);
	}

	private static void sort(StringBuilder sb, Criteria criteria) {

		if (StringUtil.isNotNull(criteria.getOrderBy())){
			sb.append(Conjunction.ORDER_BY).append(criteria.getOrderBy()).append(SPACE).append(criteria.getDirection());
		}

	}




	private static X x(StringBuilder sb, Criteria criteria) {
		X xx = null;
		List<X> xList = criteria.getListX();
		
		boolean isFirst = true;
		
		for (X x : xList) {
			Object v = x.getValue();
			if (Objects.isNull(v))
				continue;
			if (x.getConjunction() == Conjunction.GROUP_BY){
				
				xx = x;
				continue;
			}
			if (isFirst){
				sb.append(" WHERE ");
				isFirst = false;
			}else{
				sb.append(x.getConjunction().sql());
			}
			x(sb, x, criteria);
		}
		return xx;
	}

	private static void x(StringBuilder sb, X x, Criteria criteria) {

		Predicate p = x.getPredicate();
		Object v = x.getValue();

		if (v instanceof X) {
			sb.append("(");
			x(sb, x, criteria);
			sb.append(") ");
		}
		if (p == Predicate.IN || p == Predicate.NOT_IN) {
			sb.append(p.sql());
			List<Object> inList = (List<Object>) v;
			in(sb, inList);
		}
		if (p == Predicate.BETWEEN) {
			sb.append(p.sql());
			between(sb);
			
			MinMax minMax = (MinMax) v;
			List<Object> valueList = criteria.getValueList();
			valueList.add(minMax.getMin());
			valueList.add(minMax.getMax());

		} else{
			sb.append(x.getKey()).append(x.getPredicate().sql()).append(" ? ");
			Class clz = v.getClass();
			if (clz.getSuperclass().isEnum() || clz.isEnum()){
				criteria.getValueList().add(v.toString());
			}else{
				criteria.getValueList().add(v);
			}
		}

	}

	private static void between(StringBuilder sb) {

		sb.append(" ? ").append(Conjunction.AND.sql()).append(" ? ");

	}

	private static void in(StringBuilder sb, List<Object> inList) {

		if (inList == null || inList.isEmpty())
			return;

		Object v = inList.get(0);

		Class<?> vType = v.getClass();

		boolean isNumber = (vType == long.class || vType == int.class || vType == Long.class || vType == Integer.class);

		sb.append("(");

		int length = inList.size();
		if (isNumber) {
			for (int j = 0; j < length; j++) {
				Object id = inList.get(j);
				if (id == null)
					continue;
				sb.append(id);
				if (j < length - 1) {
					sb.append(",");
				}
			}
		} else {
			for (int j = 0; j < length; j++) {
				Object id = inList.get(j);
				if (id == null || StringUtil.isNullOrEmpty(id.toString()))
					continue;
				sb.append("'").append(id).append("'");
				if (j < length - 1) {
					sb.append(",");
				}
			}
		}

		sb.append(") ");

	}

	protected static void fetchSql(StringBuilder sb, Criteria criteria) {

	}

	private void check(String property) {

		if (isFetchable) {
			String str = null;
			if (property.contains(" ")) {
				String[] arr = property.split(" ");
				str = arr[0];
			} else {
				str = property;
			}
			if (str.contains(".")) {
				str = str.replace(".", "->");
				String[] xxx = str.split("->");
				if (xxx.length == 1)
					property = xxx[0];
				else
					property = xxx[1];
			} else {
				property = str;
			}

		} else {

			BeanElement be = criteria.getParsed().getElement(property);

			if (be == null) {
				throw new RuntimeException("property = " + property + ", not in " + criteria.getClz());
			}

		}
	}

	private BeanElement getBeanElement(String property) {

		String str = null;
		if (property.contains(" ")) {
			String[] arr = property.split(" ");
			str = arr[0];
		} else {
			str = property;
		}
		if (str.contains(".")) {
			str = str.replace(".", "->");
			String[] xxx = str.split("->");
			if (xxx.length == 1)
				property = xxx[0];
			else
				property = xxx[1];
		} else {
			property = str;
		}

		BeanElement be = criteria.getParsed().getElement(property);

		return be;

	}

	private boolean isBaseType_0(String property, Object v) {

		BeanElement be = getBeanElement(property);

		if (be == null) {

			String s = v.toString();
			boolean isNumeric = NumberUtil.isNumeric(s);
			if (isNumeric) {

				if (s.contains(".")) {
					return Double.valueOf(s) == 0;
				}
				return Long.valueOf(s) == 0;
			}
			return false;
		}

		Class<?> vType = be.clz;

		String s = v.toString();

		if (vType == int.class) {
			if (s.contains(".")) {
				s = s.substring(0, s.indexOf("."));
			}
			return Integer.valueOf(s) == 0;
		}
		if (vType == long.class) {
			if (s.contains(".")) {
				s = s.substring(0, s.indexOf("."));
			}
			return Long.valueOf(s) == 0;
		}
		if (vType == float.class) {
			return Float.valueOf(s) == 0;
		}
		if (vType == double.class) {
			return Double.valueOf(s) == 0;
		}
		if (vType == short.class) {
			return Short.valueOf(s) == 0;
		}
		if (vType == byte.class) {
			return Byte.valueOf(s) == 0;
		}
		if (vType == boolean.class) {
			if (s.contains(".")) {
				s = s.substring(0, s.indexOf("."));
			}
			return Integer.valueOf(s) == 0;
		}

		return false;
	}

	private boolean isNullOrEmpty(Object v) {

		Class<?> vType = v.getClass();

		if (vType == String.class) {
			return StringUtil.isNullOrEmpty(v.toString());
		}

		return false;
	}

	public interface P {

		CriteriaBuilder eq(String property, Object value);

		CriteriaBuilder lt(String property, Object value);

		CriteriaBuilder lte(String property, Object value);

		CriteriaBuilder gt(String property, Object value);

		CriteriaBuilder gte(String property, Object value);

		CriteriaBuilder not(String property, Object value);

		CriteriaBuilder like(String property, Object value);

		CriteriaBuilder between(String property, Object min, Object max);

		CriteriaBuilder in(String property, List<Object> list);

		CriteriaBuilder notIn(String property, List<Object> list);

		void under(X x);

		P x();

	}


	public Criteria get() {
		return this.criteria;
	}

	private boolean isFetchable = false;

	public class Fetchable extends CriteriaBuilder {

		@Override
		public Fetch get() {
			return (Fetch) super.get();
		}

		private void init() {
			super.isFetchable = true;
			super.instance = this;
			Criteria c = new Criteria();
			Criteria.Fetch join = c.new Fetch();
			super.criteria = join;
		}

		private void init(Class<?> clz) {
			Criteria.Fetch cj = (Criteria.Fetch) super.criteria;
			cj.setClz(clz);
			Parsed parsed = Parser.get(clz);
			cj.setParsed(parsed);
		}

		public Fetchable(Class<?> clz) {
			init();
			init(clz);
		}

		public Fetchable(Class<?> clz, Fetched fetchResult) {

			init();
			init(clz);

			xAddResultKey(fetchResult);

		}

		private Criteria.Fetch getCriteriaFetch() {
			return (Criteria.Fetch) super.criteria;
		}

		/**
		 * t->name<br>
		 * t->name.as.ne<br>
		 * 
		 * @param xExpression
		 */
		public void xAddResultKey(String xExpression) {
			getCriteriaFetch().getResultList().add(xExpression);
		}

		/**
		 * Not on Alia Name
		 * 
		 * @param xExpressionList
		 */
		public void xAddResultKey(List<String> xExpressionList) {
			for (String xExpression : xExpressionList) {
				getCriteriaFetch().getResultList().add(xExpression);
			}
		}

		private void xAddResultKey(Fetched fetchResutl) {
			if (fetchResutl == null)
				return;
			Map<String, Object> resultObjMap = fetchResutl.getResultKeyMap();
			if (resultObjMap == null || resultObjMap.isEmpty())
				return;
			List<String> xExpressionList = BeanMapUtil.toStringKeyList(resultObjMap);
			xAddResultKey(xExpressionList);
		}

	}

	///////////////////////////////////////////////////////////////////// <BR>
	/////////////////////////// REPOSITORY DEV WEB IO//////////////////// <BR>
	///////////////////////////////////////////////////////////////////// <BR>
	///////////////////////////////////////////////////////////////////// <BR>

	public static class FetchMapper {
		private Map<String, String> propertyMapperMap = new HashMap<String, String>();
		private Map<String, String> mapperPropertyMap = new HashMap<String, String>();

		public Map<String, String> getPropertyMapperMap() {
			return propertyMapperMap;
		}

		public Map<String, String> getMapperPropertyMap() {
			return mapperPropertyMap;
		}

		public void put(String property, String mapper) {
			this.propertyMapperMap.put(property, mapper);
			this.mapperPropertyMap.put(mapper, property);
		}

		public String mapper(String property) {
			return this.propertyMapperMap.get(property);
		}

		public String property(String mapper) {
			return this.mapperPropertyMap.get(mapper);
		}

		@Override
		public String toString() {
			return "FetchMapper [propertyMapperMap=" + propertyMapperMap + ", mapperPropertyMap=" + mapperPropertyMap
					+ "]";
		}
	}

}