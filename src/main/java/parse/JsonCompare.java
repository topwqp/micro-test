package parse;

import org.json.JSONObject;

/**
 * @author wangqiupeng
 * @date 2019年12月13日11:28:28
 * @desc json 比较
 */
public class JsonCompare {

    public static void main(String[] args) {
        A  originObject = new JsonCompare.A();
        originObject.setId("1");
        originObject.setName("2");
        JSONObject origin = new JSONObject(originObject);
        System.out.println(origin.toString());

        B targetObject = new JsonCompare.B();
        targetObject.setId("1");
        targetObject.setName("2");
        JSONObject target = new JSONObject(targetObject);
        System.out.println(target.toString());
        System.out.println("-----------------------");

    }



    public static class A{
        private String  name ;
        private String  id;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }


    public static class B{
        private String id ;
        private String name ;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
