package com.marklogic.client.config.search;

import java.util.List;

import org.w3c.dom.Element;

public interface Annotate  {

	public void addAnnotation(Element annotation);
	public List<Element> getAnnotations();

	
}
