package com.kite9.k9server.rest;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.Version;

public class JsonToXMLGenerator extends JsonGenerator{
	
	private Element top;
	private Document d;

	public JsonToXMLGenerator(Element into) {
		this.top = into;
		this.d = this.top.getOwnerDocument();
	}

	@Override
	public JsonGenerator setCodec(ObjectCodec oc) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ObjectCodec getCodec() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Version version() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonGenerator enable(Feature f) {
		throw new UnsupportedOperationException();
	}

	@Override
	public JsonGenerator disable(Feature f) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isEnabled(Feature f) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getFeatureMask() {
		throw new UnsupportedOperationException();
	}

	@Override
	public JsonGenerator setFeatureMask(int values) {
		throw new UnsupportedOperationException();
	}

	@Override
	public JsonGenerator useDefaultPrettyPrinter() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void writeStartArray() throws IOException {
		push("values");
	}

	private void push(String s) {
		Element e = d.createElement(s);
		top.appendChild(e);
		top = e;
	}

	@Override
	public void writeEndArray() throws IOException {
		pop();
	}

	private void pop() {
		top = (Element) top.getParentNode();
	}

	@Override
	public void writeStartObject() throws IOException {
		push("fields");
	}

	@Override
	public void writeEndObject() throws IOException {
		pop();
	}

	@Override
	public void writeFieldName(String name) throws IOException {
		push(name);
	}

	@Override
	public void writeFieldName(SerializableString name) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void writeString(String text) throws IOException {
		top.setTextContent(text);
	}

	@Override
	public void writeString(char[] text, int offset, int len) throws IOException {
		top.setTextContent(new String(text, offset, len));
	}

	@Override
	public void writeString(SerializableString text) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void writeRawUTF8String(byte[] text, int offset, int length) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void writeUTF8String(byte[] text, int offset, int length) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void writeRaw(String text) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void writeRaw(String text, int offset, int len) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void writeRaw(char[] text, int offset, int len) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void writeRaw(char c) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void writeRawValue(String text) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void writeRawValue(String text, int offset, int len) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void writeRawValue(char[] text, int offset, int len) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void writeBinary(Base64Variant bv, byte[] data, int offset, int len) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int writeBinary(Base64Variant bv, InputStream data, int dataLength) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void writeNumber(int v) throws IOException {
		top.setTextContent(Integer.toString(v));
	}

	@Override
	public void writeNumber(long v) throws IOException {
		top.setTextContent(Long.toString(v));
	}

	@Override
	public void writeNumber(BigInteger v) throws IOException {
		top.setTextContent(v.toString());
	}

	@Override
	public void writeNumber(double v) throws IOException {
		top.setTextContent(Double.toString(v));
	}

	@Override
	public void writeNumber(float v) throws IOException {
		top.setTextContent(Float.toString(v));
	}

	@Override
	public void writeNumber(BigDecimal v) throws IOException {
		top.setTextContent(v.toString());
	}

	@Override
	public void writeNumber(String encodedValue) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void writeBoolean(boolean state) throws IOException {
		top.setTextContent(Boolean.toString(state));
	}

	@Override
	public void writeNull() throws IOException {
		top.setTextContent("null");
	}

	@Override
	public void writeObject(Object pojo) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void writeTree(TreeNode rootNode) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public JsonStreamContext getOutputContext() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void flush() throws IOException {
	}

	@Override
	public boolean isClosed() {
		return false;
	}

	@Override
	public void close() throws IOException {
	}
	
	
}
