package com.cvte.maxhub.mxboard.noteio;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * @author zhengshaoting
 * @version v1.0
 * @data 2018/11/22
 * @description
 */
@RunWith(RobolectricTestRunner.class)
public class CustomMapperTest {
	
	/**
	 * 把java对象的成员变量名开头有_或my，序列化时去掉
	 */
	private static class FieldPrefixStrippingMapper extends MapperWrapper {
		public FieldPrefixStrippingMapper(final Mapper wrapped) {
			super(wrapped);
		}
		
		@Override
		public String serializedMember(final Class<?> type, String memberName) {
			if (memberName.startsWith("_")) {
				// _blah -> blah
				memberName = memberName.substring(1); // chop off leading char (the underscore)
			} else if (memberName.startsWith("my")) {
				// myBlah -> blah
				memberName = memberName.substring(2, 3).toLowerCase() + memberName.substring(3);
			}
			return super.serializedMember(type, memberName);
		}
		
		@Override
		public String realMember(final Class<?> type, final String serialized) {
			final String fieldName = super.realMember(type, serialized);
			// Not very efficient or elegant, but enough to get the point across.
			// Luckily the CachingMapper will ensure this is only ever called once per field per class.
			try {
				type.getDeclaredField("_" + fieldName);
				return "_" + fieldName;
			} catch (final NoSuchFieldException e) {
				try {
					final String myified = "my" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
					type.getDeclaredField(myified);
					return myified;
				} catch (final NoSuchFieldException e2) {
					return fieldName;
				}
			}
		}
	}
	
	@Test
	public void testUserDefinedMappingCanAlterFieldName() {
		
		XStream xstream = new XStream() {
			@Override
			protected MapperWrapper wrapMapper(final MapperWrapper next) {
				return new FieldPrefixStrippingMapper(next);
			}
		};
		xstream.allowTypesByWildcard(this.getClass().getName() + "$*");
		xstream.alias("thing", ThingWithStupidNamingConventions.class);
		
		final ThingWithStupidNamingConventions in = new ThingWithStupidNamingConventions("Joe", "Walnes", 10);
		final String expectedXml = ""
				+ "<thing>\n"
				+ "  <firstName>Joe</firstName>\n" // look, no underscores!
				+ "  <lastName>Walnes</lastName>\n"
				+ "  <age>10</age>\n"
				+ "</thing>";
		
		ThingWithStupidNamingConventions conventions = xstream.fromXML(expectedXml);
	}
	
	public static class ThingWithStupidNamingConventions extends StandardObject {
		private static final long serialVersionUID = 200503L;
		String _firstName;
		String lastName;
		int myAge;
		
		public ThingWithStupidNamingConventions(final String firstname, final String lastname, final int age) {
			_firstName = firstname;
			lastName = lastname;
			myAge = age;
		}
	}
}
