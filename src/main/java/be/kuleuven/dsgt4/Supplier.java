package be.kuleuven.dsgt4;

public class Supplier {
        private String id;
        private int apiKey;
        private String name;
        private String baseUrl;

        public Supplier(){

        }

        public Supplier( String baseUrl, String name, String id, int apiKey) {
                this.apiKey = apiKey;
                this.baseUrl = baseUrl;
                this.name = name;
                this.id = id;
        }

        public String getId() {
                return id;
        }

        public void setId(String id) {
                this.id = id;
        }

        public int getApiKey() {
                return apiKey;
        }

        public void setApiKey(int apiKey) {
                this.apiKey = apiKey;
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public String getBaseUrl() {
                return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
                this.baseUrl = baseUrl;
        }
}
