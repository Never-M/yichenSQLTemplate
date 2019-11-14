
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import java.util.Map;


/**
 *
 * @author zjz20
 */
public class TemplateBuilder {
        
        public TemplateBuilder withTable(String tableName, Message objDescriptor) {
                Map<Descriptors.FieldDescriptor, Object> fields = 
                        objDescriptor.getAllFields();
                return this;
        }
}
