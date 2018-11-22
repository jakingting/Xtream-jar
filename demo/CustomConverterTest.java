package com.cvte.maxhub.mxboard.noteio;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.io.xml.Xpp3Driver;

import org.junit.Test;


/**
 * @author zhengshaoting
 * @version v1.0
 * @data 2018/11/22
 * @description
 */
public class CustomConverterTest {
	/**
	 * 对所有Long 反序列化时+1，序列化时-1
	 */
	private final class DoubleConverter implements SingleValueConverter {
		
		@Override
		public boolean canConvert(final Class<?> type) {
			return type == long.class || type == Long.class;
		}
		
		@Override
		public String toString(final Object obj) {
			
			return String.valueOf(((Long)obj -1));
		}
		
		@Override
		public Object fromString(final String str) {
			return Long.parseLong(str) + 1;
		}
	}
	
	public static class LongWrapper {
		Long d;
		
		public LongWrapper(final long d) {
			this.d = new Long(d);
		}
	}
	
	@Test
	public void testWrongObjectTypeReturned() {
		XStream xstream = new XStream(new Xpp3Driver());
		xstream.allowTypesByWildcard(CustomConverterTest.class.getPackage().getName() + ".*objects.**");
		xstream.allowTypesByWildcard(this.getClass().getName() + "$*");
		xstream.alias("dw", LongWrapper.class);
		xstream.registerConverter(new DoubleConverter());
		
		final String xml = "" //
				+ "<dw>\n"
				+ "  <d>92</d>\n"
				+ "</dw>";
		LongWrapper longWrapper = xstream.fromXML(xml);
		System.out.println(longWrapper.d);
	}
	
}
