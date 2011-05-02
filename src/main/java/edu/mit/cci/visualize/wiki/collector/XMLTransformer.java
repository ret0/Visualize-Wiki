package edu.mit.cci.visualize.wiki.collector;

import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class XMLTransformer {

    private static final XStream xstream = new XStream();

    public static List<Revision> getRevisionFromXML(){
        xstream.registerConverter(new Converter() {

            @Override
            public boolean canConvert(@SuppressWarnings("rawtypes") final Class clazz) {
                return false;
            }

            @Override
            public Object unmarshal(final HierarchicalStreamReader reader,
                                    final UnmarshallingContext context) {
                // TODO Auto-generated method stub
                reader.moveDown();

                reader.moveUp();
                return null;
            }

            @Override
            public void marshal(final Object object,
                                final HierarchicalStreamWriter writer,
                                final MarshallingContext context) {
                throw new RuntimeException("Marshalling not supported");
            }
        });
        return new LinkedList<Revision>();
    }

}
