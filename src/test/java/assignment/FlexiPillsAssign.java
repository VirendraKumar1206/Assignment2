
	package assignment;

	import java.io.IOException;
	import java.nio.charset.StandardCharsets;
	import java.time.Duration;
	import java.util.List;

	import org.apache.hc.client5.http.classic.methods.HttpPost;
	import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
	import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
	import org.apache.hc.client5.http.impl.classic.HttpClients;
	import org.apache.hc.core5.http.ParseException;
	import org.apache.hc.core5.http.io.entity.EntityUtils;
	import org.apache.hc.core5.http.io.entity.StringEntity;
	import org.json.JSONObject;
	import org.openqa.selenium.By;
	import org.openqa.selenium.JavascriptExecutor;
	import org.openqa.selenium.WebDriver;
	import org.openqa.selenium.WebElement;
	import org.openqa.selenium.chrome.ChromeDriver;
	import org.openqa.selenium.support.ui.ExpectedConditions;
	import org.openqa.selenium.support.ui.WebDriverWait;
	import org.testng.Assert;
	import org.testng.annotations.BeforeTest;
	import org.testng.annotations.Test;

	public class FlexiPillsAssign {
	    WebDriver driver;
	    JavascriptExecutor js;
	    String token;

	    @BeforeTest
	    public void setup() throws InterruptedException, IOException, ParseException {
	        driver = new ChromeDriver();
	        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
	        driver.get("https://flexipill-ui-new-staging.vercel.app/");
	        driver.manage().window().maximize();
	        js = (JavascriptExecutor) driver;

	        // Perform API login to get token
	        token = loginApi("1111111111", "1111");// here i'm logging through an API to retrieve a token for authentication.
	    }

	    public String loginApi(String phoneNumber, String otpCode) throws IOException, ParseException { // Method to log in to the API and retrieve an authentication token.
	        String loginUrl = "https://backendstaging.platinumrx.in/auth/login"; // Sets the login URL.
	        CloseableHttpClient client = HttpClients.createDefault(); // Creating an HTTP client.
	        HttpPost httpPost = new HttpPost(loginUrl); // Creating an HTTP POST request.

	        JSONObject json = new JSONObject(); // Building a JSON object with the phone number and OTP code.
	        json.put("phone_number", phoneNumber);
	        json.put("otp_code", otpCode);

	        StringEntity entity = new StringEntity(json.toString(), StandardCharsets.UTF_8); // it Wraps the JSON object in a StringEntity.
	        httpPost.setEntity(entity); // Sets the request entity.
	        httpPost.setHeader("Content-Type", "application/json"); // Sets the content type to application/json.

	        CloseableHttpResponse response = client.execute(httpPost); // Executes the POST request.
	        String responseBody = EntityUtils.toString(response.getEntity()); // Processes the response.
	        JSONObject responseJson = new JSONObject(responseBody); // Converts the response body to a JSON object.

	        Assert.assertEquals(response.getCode(), 200); // using the assertion to verify the status code
	        client.close(); 

	        return responseJson.getString("token"); //it will Returns the token from the JSON response.
	    }

	    @Test(priority = 1)
	    public void login() throws InterruptedException {
	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
	        driver.findElement(By.linkText("Login")).click();
	        Thread.sleep(3000);
	        driver.findElement(By.id(":r2:")).sendKeys("1111111111");
	        Thread.sleep(3000);
	        driver.findElement(By.xpath("//p[text()='Continue']")).click();
	        Thread.sleep(3000);
	        driver.findElement(By.id(":r4:")).sendKeys("1");
	        Thread.sleep(3000);
	        driver.findElement(By.id(":r5:")).sendKeys("1");
	        Thread.sleep(3000);
	        driver.findElement(By.id(":r6:")).sendKeys("1");
	        Thread.sleep(3000);
	        driver.findElement(By.id(":r7:")).sendKeys("1");
	        Thread.sleep(3000);
	    }

	    @Test(priority = 2)
	    public void addToCart() throws InterruptedException, IOException, ParseException { 
	        WebElement elem = driver.findElement(By.xpath("//p[text()='The PlatinumRx Advantage']")); 
	        js.executeScript("arguments[0].scrollIntoView()", elem); 
	        Thread.sleep(7000); 
	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15)); 
	        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//span[text()='Add to Cart'])[5]")));
	        driver.findElement(By.xpath("(//span[text()='Add to Cart'])[5]")).click();

	        addToCartApi(1110806, 2); // Calls the addToCartApi method to add an item to the cart through the API.
	    }

	    public void addToCartApi(int drugCode, int increaseQuantityBy) throws IOException, ParseException { // Method to add an item to the cart via an API call.
	        String addToCartUrl = "https://backendstaging.platinumrx.in/cart/addItem"; // Sets the add-to-cart URL.
	        CloseableHttpClient client = HttpClients.createDefault(); // Creates an HTTP client.
	        HttpPost httpPost = new HttpPost(addToCartUrl); // Creates an HTTP POST request.

	        JSONObject json = new JSONObject(); // Builds a JSON object with the drug code and quantity.
	        json.put("increaseQuantityBy", String.valueOf(increaseQuantityBy));
	        json.put("drugCode", drugCode);

	        StringEntity entity = new StringEntity(json.toString(), StandardCharsets.UTF_8); // Wraps the JSON object in a StringEntity.
	        httpPost.setEntity(entity); // Sets the request entity.
	        httpPost.setHeader("Content-Type", "application/json"); // Sets the content type to application/json.
	        httpPost.setHeader("Authorization", "Bearer " + token); // Sets the authorization header.

	        CloseableHttpResponse response = client.execute(httpPost); // Executes the POST request.
	        String responseBody = EntityUtils.toString(response.getEntity()); // Processes the response.
	        JSONObject responseJson = new JSONObject(responseBody); // Converts the response body to a JSON object.

	        Assert.assertEquals(response.getCode(), 200); // using the assertion to verify the status code.
	        Assert.assertTrue(responseJson.getBoolean("success")); // Asserts that the success flag is true.
	        client.close(); // Closes the HTTP client.
	    }

	    @Test(priority = 3)
	    public void createOrder() throws InterruptedException, IOException, ParseException {
	        driver.findElement(By.xpath("//button[@class='Header_cartButton__Giyrb']")).click();

	        WebElement dropdown = driver.findElement(By.cssSelector(".AddToCartDropdown_arrow__pFEjt"));
	        dropdown.click();

	        List<WebElement> options = driver
	                .findElements(By.xpath("//div[@class='quantity-select AddToCartDropdown_quantity-list__7edu0']"));

	        for (WebElement option : options) {
	            if (option.getText().equals("9")) {
	                Thread.sleep(3000);
	                option.click();
	                break;
	            }
	        }
	        Thread.sleep(3000);

	        WebElement scrl = driver.findElement(By.xpath("//h4[text()='Contact Us']"));
	        js.executeScript("arguments[0].scrollIntoView()", scrl);
	        Thread.sleep(3000);

	        driver.findElement(By.xpath("(//input[@type='radio'])[1]")).click();
	        Thread.sleep(3000);
	        driver.findElement(By.xpath("//button[text()='Place Order']")).click();

	        // Perform API call to create order
	        createOrderApi("COD", "SEARCH", "test", "test-block test-city test-state 577201", "9876543219", 23, "male", 577201, "test-city", "test-state"); // Calls the createOrderApi method to create an order through the API.
	    }

	    public void createOrderApi(String paymentType, String orderType, String patientName, String patientAddress, String patientMobileNumber, int patientAge, String patientGender, int pincode, String city, String state) throws IOException, ParseException { // Method to create an order via an API call.
	        String createOrderUrl = "https://backendstaging.platinumrx.in/orders/initiateOrder"; // Sets the create order URL.
	        CloseableHttpClient client = HttpClients.createDefault(); // Creates an HTTP client.
	        HttpPost httpPost = new HttpPost(createOrderUrl); // Creates an HTTP POST request.

	        JSONObject json = new JSONObject(); // Builds a JSON object with order details.
	        json.put("paymentType", paymentType);
	        json.put("orderType", orderType);
	        json.put("patientName", patientName);
	        json.put("patientAddress", patientAddress);
	        json.put("patientMobileNumber", patientMobileNumber);
	        json.put("patientAge", String.valueOf(patientAge));
	        json.put("patientGender", patientGender);
	        json.put("pincode", pincode);
	        json.put("city", city);
	        json.put("state", state);

	        StringEntity entity = new StringEntity(json.toString(), StandardCharsets.UTF_8); // Wraps the JSON object in a StringEntity.
	        httpPost.setEntity(entity); // Sets the request entity.
	        httpPost.setHeader("Content-Type", "application/json"); // Sets the content type to application/json.
	        httpPost.setHeader("Authorization", "Bearer " + token); // Sets the authorization header.

	        CloseableHttpResponse response = client.execute(httpPost); // Executes the POST request.
	        String responseBody = EntityUtils.toString(response.getEntity()); // Processes the response.
	        JSONObject responseJson = new JSONObject(responseBody); // Converts the response body to a JSON object.

	        Assert.assertEquals(response.getCode(), 200); // Asserts that the response status code is 200 (OK).
	        Assert.assertTrue(responseJson.getBoolean("success")); // Asserts that the success flag is true.
	        client.close(); // Closes the HTTP client.
	    }

	    // Negative Scenarios

	    @Test(priority = 4)
	    public void testInvalidLogin() throws IOException, ParseException { // Method to test invalid login scenario.
	        String loginUrl = "https://backendstaging.platinumrx.in/auth/login"; // Sets the login URL.
	        CloseableHttpClient client = HttpClients.createDefault(); // Creates an HTTP client.
	        HttpPost httpPost = new HttpPost(loginUrl); // Creates an HTTP POST request.

	        JSONObject json = new JSONObject(); // Builds a JSON object with invalid login details.
	        json.put("phone_number", "1111111111");
	        json.put("otp_code", "wrong");

	        StringEntity entity = new StringEntity(json.toString(), StandardCharsets.UTF_8); // Wraps the JSON object in a StringEntity.
	        httpPost.setEntity(entity); // Sets the request entity.
	        httpPost.setHeader("Content-Type", "application/json"); // Sets the content type to application/json.

	        CloseableHttpResponse response = client.execute(httpPost); // Executes the POST request.
	        String responseBody = EntityUtils.toString(response.getEntity()); // Processes the response.
	        JSONObject responseJson = new JSONObject(responseBody); // Converts the response body to a JSON object.

	        Assert.assertEquals(response.getCode(), 401); // Asserts that the response status code is 401 (Unauthorized).
	        client.close(); // Closes the HTTP client.
	    }

	    @Test(priority = 5)
	    public void testInvalidAddToCart() throws IOException, ParseException { // Method to test invalid add-to-cart scenario.
	        String addToCartUrl = "https://backendstaging.platinumrx.in/cart/addItem"; // Sets the add-to-cart URL.
	        CloseableHttpClient client = HttpClients.createDefault(); // Creates an HTTP client.
	        HttpPost httpPost = new HttpPost(addToCartUrl); // Creates an HTTP POST request.

	        JSONObject json = new JSONObject(); // Builds a JSON object with invalid drug code.
	        json.put("increaseQuantityBy", "2");
	        json.put("drugCode", "invalid");

	        StringEntity entity = new StringEntity(json.toString(), StandardCharsets.UTF_8); // Wraps the JSON object in a StringEntity.
	        httpPost.setEntity(entity); // Sets the request entity.
	        httpPost.setHeader("Content-Type", "application/json"); // Sets the content type to application/json.
	        httpPost.setHeader("Authorization", "Bearer " + token); // Sets the authorization header.

	        CloseableHttpResponse response = client.execute(httpPost); // Executes the POST request.
	        String responseBody = EntityUtils.toString(response.getEntity()); // Processes the response.
	        JSONObject responseJson = new JSONObject(responseBody); // Converts the response body to a JSON object.

	        Assert.assertNotEquals(response.getCode(), 200); // Asserts that the response status code is not 200 (OK).
	        client.close(); // Closes the HTTP client.
	    }

	    @Test(priority = 6)
	    public void testInvalidCreateOrder() throws IOException,  ParseException { // Method to test invalid create order scenario.
	        String createOrderUrl = "https://backendstaging.platinumrx.in/orders/initiateOrder"; // Sets the create order URL.
	        CloseableHttpClient client = HttpClients.createDefault(); // Creates an HTTP client.
	        HttpPost httpPost = new HttpPost(createOrderUrl); // Creates an HTTP POST request.

	        JSONObject json = new JSONObject(); // Builds a JSON object with invalid order details.
	        json.put("paymentType", "COD");
	        json.put("orderType", "SEARCH");
	        json.put("patientName", "");
	        json.put("patientAddress", "");
	        json.put("patientMobileNumber", "");
	        json.put("patientAge", "");
	        json.put("patientGender", "");
	        json.put("pincode", 0);
	        json.put("city", "");
	        json.put("state", "");

	        StringEntity entity = new StringEntity(json.toString(), StandardCharsets.UTF_8); // Wraps the JSON object in a StringEntity.
	        httpPost.setEntity(entity); // Sets the request entity.
	        httpPost.setHeader("Content-Type", "application/json"); // Sets the content type to application/json.
	        httpPost.setHeader("Authorization", "Bearer " + token); // Sets the authorization header.

	        CloseableHttpResponse response = client.execute(httpPost); // Executes the POST request.
	        String responseBody = EntityUtils.toString(response.getEntity()); // Processes the response.
	        JSONObject responseJson = new JSONObject(responseBody); // Converts the response body to a JSON object.

	        Assert.assertNotEquals(response.getCode(), 200); // Asserts that the response status code is not 200 (OK).
	        client.close(); // Closes the HTTP client.
	    }
	}

	        		
	        		
	        		
	        		
