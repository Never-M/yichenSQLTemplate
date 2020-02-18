package engine;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;


/**
 *
 * @author zjz20
 */
public class TemplateBuilder {
        private Class resultType;
        private String query;
        
        /**
         * Example: query = "FROM AUser user 
         * JOIN CreditCard card ON card.userId = user.id WHERE user.joinDate > '2020-1-1'"
         * @param query
         * @return 
         */
        public TemplateBuilder withSelect(String query) {
                this.query = query;
                return this;
        }
        
        public TemplateBuilder withResultType(Class resultType) {
                this.resultType = resultType;
                return this;
        }
        
        public <ReturnType> List<ReturnType> run(Class classType) throws NoSuchMethodException, 
                                                                         InstantiationException, 
                                                                         IllegalAccessException, 
                                                                         IllegalArgumentException, 
                                                                         InvocationTargetException {
                 throw new UnsupportedOperationException();
        }
        
        public static String completeSelectQuery(String query, Class resultType){
                StringBuilder sb = new StringBuilder("select ");
                for (Field alias : resultType.getDeclaredFields()){
                        Class aliasType;
                        if (alias.getType().isArray()) {
                                aliasType = alias.getType().getComponentType();
                        } else {
                                aliasType = alias.getType();
                        }
                        for (Field field : aliasType.getDeclaredFields()){
                               sb.append(alias.getName());
                               sb.append('.');
                               sb.append(field.getName());
                               sb.append(',');
                        }
                }
                sb.setLength(sb.length() - 1);
                sb.append(' ');
                sb.append(query);
                return sb.toString();
        }
}
