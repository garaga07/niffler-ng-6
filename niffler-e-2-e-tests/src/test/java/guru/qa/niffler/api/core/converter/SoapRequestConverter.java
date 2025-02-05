package guru.qa.niffler.api.core.converter;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import okhttp3.RequestBody;
import org.w3c.dom.Document;
import retrofit2.Converter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static guru.qa.niffler.api.core.converter.SoapConverterFactory.XML;

final class SoapRequestConverter<T> implements Converter<T, RequestBody> {
    final XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
    final JAXBContext context;
    final String namespace;

    SoapRequestConverter(JAXBContext context, String namespace) {
        this.context = context;
        this.namespace = namespace;
    }

    @Override
    public RequestBody convert(final T value) {
        try(ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream()) {
            SOAPMessage soapMessage = MessageFactory.newInstance().createMessage();
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

            Marshaller marshaller = context.createMarshaller();
            marshaller.marshal(value, document);

            soapMessage.getSOAPBody().addDocument(document);
            SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();
            envelope.addNamespaceDeclaration("tns", namespace);
            soapMessage.writeTo(byteOutputStream);

            return RequestBody.create(XML, byteOutputStream.toByteArray());
        } catch (SOAPException | ParserConfigurationException | JAXBException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}