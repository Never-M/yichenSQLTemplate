package engine;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
/**
 *
 * @author zjz20
 */
public class CompleteStringTest {
        
        public CompleteStringTest() {
        }
        
        public static class UserHasOneCreditCard {
                public static class UserInfo {
                        public int id;
                        public String username;
                }

                public static class Cards {
                        public int id;
                        public String number;
                }
                public UserInfo user;
                public Cards card;
        }
        
         @Test
         public void testOneToOneCase() {
                 String query = "FROM AUser user JOIN CreditCard card ON "
                         + "card.userId = user.id WHERE user.joinDate > '2020-1-1'";
                 String completedquery = 
                        TemplateBuilder.completeSelectQuery(query, UserHasOneCreditCard.class);
                 String real = "select user.id,user.username,card.id,"
                         + "card.number FROM AUser user JOIN CreditCard card ON "
                         + "card.userId = user.id WHERE user.joinDate > '2020-1-1'";
                 Assert.assertEquals(real, completedquery);
         }
         
         public static class UserHasManyCreditCards {
                public static class UserInfo {
                        public int id;
                        public String username;
                }

                public static class Cards {
                        public int id;
                        public String number;
                }
                public UserInfo user;
                public Cards[] cards;
        }
         
         @Test
         public void testOneToManyCase() {
                 String query = "FROM AUser user JOIN CreditCard cards ON "
                         + "cards.userId = user.id WHERE user.joinDate > '2020-1-1'";
                 String completedquery = 
                        TemplateBuilder.completeSelectQuery(query, UserHasManyCreditCards.class);
                 String real = "select user.id,user.username,cards.id,"
                         + "cards.number FROM AUser user JOIN CreditCard cards ON "
                         + "cards.userId = user.id WHERE user.joinDate > '2020-1-1'";
                 Assert.assertEquals(real, completedquery);
         }
}
