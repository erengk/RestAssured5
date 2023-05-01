import Model.Location;
import Model.Place;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class ZippoTest {

    @Test
    public void test() {
        given()
                //Hazırlık işlemleri : (token, send body, parametreler)

                .when()
                //endpoint (url), metodu

                .that()
        //assertion, test, data işlemleri

        ;
    }

    @Test
    public void statusCodeTest() {
        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                //.log().body() // dönen body json datası
                //.log().all() // tüm json datası
                .statusCode(200) // dönüş kodu 200 mü

        ;
    }

    @Test
    public void contentTypeTest() {
        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body() // dönen body json datası
                //.log().all() // tüm json datası
                .statusCode(200) // dönüş kodu 200 mü
                .contentType(ContentType.JSON) // dönen sonuç json mu

        ;
    }

    @Test
    public void checkCountryInResponseBodyTest() {
        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body() // dönen body json datası
                .statusCode(200) // dönüş kodu 200 mü
                .body("country", equalTo("United States")) // body nin country değişkeni "United States" e eşit mi

        // pm.response.json().id -> body.id
        ;
    }

    @Test
    public void checkStateInResponseBodyTest() {
        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body() // dönen body json datası
                .statusCode(200) // dönüş kodu 200 mü
                .body("places[0].state", equalTo("California")) // body nin state değişkeni "California" ya eşit mi
        ;
    }

    @Test
    public void checkHasItemInResponseBodyTest() {
        given()

                .when()
                .get("http://api.zippopotam.us/tr/01000")

                .then()
                //.log().body() // dönen body json datası
                .statusCode(200) // dönüş kodu 200 mü
                .body("places.'place name'", hasItem("Dörtağaç Köyü")) // bütün place name lerin herhangi birinde Dörtağaç Köyü var mı
        ;
    }

    @Test
    public void bodyArrayHasSizeTest() {
        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                //.log().body() // dönen body json datası
                .statusCode(200) // dönüş kodu 200 mü
                .body("places", hasSize(1))
        ;
    }

    @Test
    public void combiningTest() {
        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                //.log().body() // dönen body json datası
                .statusCode(200) // dönüş kodu 200 mü
                .body("places", hasSize(1))
                .body("places.state", hasItem("California"))
                .body("places[0].'place name'", equalTo("Beverly Hills"))
        ;
    }

    @Test
    public void pathParamTest() {
        given()
                .pathParam("ulke", "us")
                .pathParam("postaKod", 90210)
                .log().uri() // request Link

                .when()
                .get("http://api.zippopotam.us/{ulke}/{postaKod}")


                .then()
                .statusCode(200)
        //.log().body()

        ;
    }

    @Test
    public void queryParamTest() {
        given()
                .param("page", 1)
                .log().uri() // request Link


                .when()
                .get("https://gorest.co.in/public/v1/users")

                .then()
                .statusCode(200)

        ;
    }

    @Test
    public void queryParamTest2() {
        // https://gorest.co.in/public/v1/users
        // bu linkteki 1 den 10 kadar sayfaları çağırdığınızda response daki donen page degerlerinin
        // çağrılan page nosu ile aynı olup olmadığını kontrol ediniz.

        for (int i = 1; i < 10; i++) {

            given()

                    .param("page", i)
                    .log().uri() // request Link

                    .when()
                    .get("https://gorest.co.in/public/v1/users")

                    .then()
                    .statusCode(200)
                    .body("meta.pagination.page", equalTo(i))

            ;
        }
    }

    RequestSpecification requestSpecification;
    ResponseSpecification responseSpecification;

    @BeforeClass
    public void Setup() {
        baseURI = "https://gorest.co.in/public/v1";

        requestSpecification = new RequestSpecBuilder()
                .log(LogDetail.URI)
                .setContentType(ContentType.JSON)
                .build();

        responseSpecification = new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .expectStatusCode(200)
                .log(LogDetail.BODY)
                .build();
    }

    @Test
    public void Test1() {
        given()
                .param("page", 1)
                .spec(requestSpecification)


                .when()
                .get("/users")

                .then()
                .spec(responseSpecification)

        ;
    }

    @Test
    public void extractingJsonPath() {

        String countryName =
                given()

                        .when()
                        .get("http://api.zippopotam.us/us/90210")

                        .then()
                        .extract().path("country");
        System.out.println("countryName = " + countryName);
        Assert.assertEquals(countryName, "United States");
    }

    @Test
    public void extractingJsonPath2() {

        String placeName =
                given()

                        .when()
                        .get("http://api.zippopotam.us/us/90210")

                        .then()
                        .extract().path("places[0]['place name']");
        System.out.println("placeName = " + placeName);
        Assert.assertEquals(placeName, "Beverly Hills");
    }

    @Test
    public void extractingJsonPath3() {
        // https://gorest.co.in/public/v1/users  dönen değerdeki limit bilgisini yazdırınız.

        int limit =
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        .log().body()
                        .statusCode(200)
                        .extract().path("meta.pagination.limit");
        System.out.println("limit = " + limit);
        Assert.assertEquals(limit, 10);

    }

    @Test
    public void extractingJsonPath4() {
        // https://gorest.co.in/public/v1/users  dönen değerdeki limit bilgisini yazdırınız.

        List<Integer> idList =
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        .statusCode(200)
                        .extract().path("data.id");
        System.out.println("idList = " + idList);
    }

    @Test
    public void extractingJsonPath5() {
        // https://gorest.co.in/public/v1/users  dönen değerdeki limit bilgisini yazdırınız.

        List<String> nameList =
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        .log().body()
                        .statusCode(200)
                        .extract().path("data.name");
        System.out.println("nameList = " + nameList);
    }

    @Test
    public void extractingJsonPath6() {
        // https://gorest.co.in/public/v1/users  dönen değerdeki limit bilgisini yazdırınız.

        List<String> eMailList =
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        .log().body()
                        .statusCode(200)
                        .extract().path("data.email");
        System.out.println("eMailList = " + eMailList);
    }

    @Test
    public void extractingJsonPathResponsAll() {
        // https://gorest.co.in/public/v1/users  dönen değerdeki limit bilgisini yazdırınız.

        Response response =
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        .log().body()
                        .statusCode(200)
                        .extract().response();
        List<Integer> idList = response.path("data.id");
        List<String> nameList = response.path("data.name");
        int limit = response.path("meta.pagination.limit");

        System.out.println("limit = " + limit);
        System.out.println("nameList = " + nameList);
        System.out.println("idList = " + idList);

        Assert.assertTrue(nameList.contains("Gouranga Panicker"));
        Assert.assertTrue(idList.contains(1203761));
        Assert.assertEquals(limit, 10, "test sonucu hatalı");
    }

    @Test
    public void extractJsonAll_POJO()
    {  // POJO : JSON nesnesi : locationNesnesi
        Location locationNesnesi=
                given()
                        .when()
                        .get("http://api.zippopotam.us/us/90210")

                        .then()
                        //.log().body()
                        .extract().body().as(Location.class)
                // // Location şablonuna
                ;

        System.out.println("locationNesnesi.getCountry() = " +
                locationNesnesi.getCountry());

        for(Place p: locationNesnesi.getPlaces())
            System.out.println("p = " + p);

        System.out.println(locationNesnesi.getPlaces().get(0).getPlacename());
    }


    @Test
    public void extractPOJO_Soru(){
        // aşağıdaki endpointte(link)  Dörtağaç Köyü ait diğer bilgileri yazdırınız

        Location adana=
                given()
                        .when()
                        .get("http://api.zippopotam.us/tr/01000")

                        .then()
                        //.log().body()
                        .statusCode(200)
                        .extract().body().as(Location.class)
                ;

        for(Place p: adana.getPlaces())
            if (p.getPlacename().equalsIgnoreCase("Dörtağaç Köyü"))
            {
                System.out.println("p = " + p);
            }
    }
}

/*
PM                            RestAssured
body.country                  body("country")
body.'post code'              body("post code")
body.places[0].'place name'   body("places[0].'place name'")
body.places.'place name'      body("places.'place name'")
bütün place nameleri bir arraylist olarak verir

{
    "post code": "90210",
    "country": "United States",
    "country abbreviation": "US",
    "places": [
        {
            "place name": "Beverly Hills",
            "longitude": "-118.4065",
            "state": "California",
            "state abbreviation": "CA",
            "latitude": "34.0901"
        }
    ]
}

http://api.zippopotam.us/us/90210    path PARAM

https://sonuc.osym.gov.tr/Sorgu.aspx?SonucID=9617  Query PARAM

 */
