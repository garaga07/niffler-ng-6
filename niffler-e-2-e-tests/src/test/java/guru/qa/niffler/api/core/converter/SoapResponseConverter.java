package guru.qa.niffler.api.core.converter;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.MimeHeaders;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import okhttp3.ResponseBody;
import org.w3c.dom.Document;
import retrofit2.Converter;

import javax.xml.stream.XMLInputFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

final class SoapResponseConverter<T> implements Converter<ResponseBody, T> {
    private static final Logger LOGGER = Logger.getLogger(SoapResponseConverter.class.getName());

    private final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
    private final JAXBContext context;
    private final Class<T> type;

    SoapResponseConverter(JAXBContext context, Class<T> type) {
        this.context = context;
        this.type = type;

        // Prevent XML External Entity attacks (XXE).
        xmlInputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        try (InputStream is = value.byteStream()) {
            boolean isEmptyResponse = value.contentLength() == 0;

            // Если сервер вернул пустой ответ, просто логируем и возвращаем null
            if (isEmptyResponse) {
                LOGGER.warning("Received an empty SOAP response from server.");
                return null;
            }

            MimeHeaders headers = new MimeHeaders();
            if (value.contentType() != null) {
                headers.addHeader("Content-Type", value.contentType().toString());
            }

            SOAPMessage response = MessageFactory.newInstance().createMessage(headers, is);
            Document document = response.getSOAPBody().extractContentAsDocument();

            return context.createUnmarshaller().unmarshal(document, type).getValue();
        } catch (SOAPException e) {
            throw new RuntimeException("SOAP parsing error: " + e.getMessage(), e);
        } catch (JAXBException e) {
            throw new RuntimeException("JAXB unmarshalling error: " + e.getMessage(), e);
        }
    }
}