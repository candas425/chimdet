package ro.utcn;

import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import ro.utcn.backend.backendservices.*;
import ro.utcn.backend.model.*;
import ro.utcn.backend.model.enums.TipConcediu;
import ro.utcn.backend.model.enums.TipFirma;
import ro.utcn.backend.model.enums.TipProdus;
import ro.utcn.config.AppConfig;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Base class for testing
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class}, loader = AnnotationConfigContextLoader.class)
public abstract class BaseTest {

    //client/angajat
    protected static final String CNP = "1940222203238";
    protected static final String NUME = "Davidescu Lucian";
    protected static final String SERIA = "HD591087";
    protected static final String ADRESA = "Str. Almasului; nr.8; bl. 3";

    //firma
    protected static final String NUME_FIRMA = "CHIMDET";
    protected static final String SEDIUL = "Orastie";
    protected static final String CUI = "RO18544129";
    protected static final String REGCOM = "J20/494/2006";
    protected static final String JUDET = "HUNEDOARA";
    protected static final String BANCA = "TRANSILVANIA";

    //produs
    protected static final String NUME_PRODUS = "SapunLichid";
    protected static final String IDENTIFICATOR_PRODUS = "0007";

    //user
    protected static final String USERNAME = "admin";
    protected static final String PASSWORD = "parola";

    @Autowired
    private CleanDbService cleanDbService;
    @Autowired
    protected EmployeeService employeeService;
    @Autowired
    protected BusinessService businessService;
    @Autowired
    protected HolidayService holidayService;
    @Autowired
    protected CommandService commandService;
    @Autowired
    protected ProductService productService;
    @Autowired
    protected PriceService priceService;
    @Autowired
    protected GeneralSettingService generalSettingService;
    @Autowired
    protected UserService userService;

    @After
    public void cleanUpDB() {
        cleanDbService.cleanDb();
    }

    protected Employee createAngajat() {
        return createAngajat(CNP, NUME, 15, LocalDate.now());
    }

    protected Employee createAngajat(String cnp, String nume, int zileConcediuRamese, LocalDate dataAngajarii) {
        Employee employee = new Employee();
        employee.setCnp(cnp);
        employee.setDataAngajarii(dataAngajarii);
        employee.setNume(nume);
        employee.setZileConcediuRamase(zileConcediuRamese);
        return employee;
    }

    protected Business createBusiness() {
        return createBusiness(NUME_FIRMA, BANCA, CUI, JUDET, REGCOM, SEDIUL, TipFirma.NORMAL);
    }

    protected Business createBusiness(String nume, String banca, String cui, String judet, String regCom, String sediul, TipFirma tipFirma) {
        Business business = new Business();
        business.setNume(nume);
        business.setMail(banca);
        business.setCui(cui);
        business.setJudet(judet);
        business.setRegCom(regCom);
        business.setSediul(sediul);
        business.setTip(tipFirma);
        business.setAvailableInCommands("Adevarat");
        return business;
    }

    protected Command createComanda(LocalDateTime localDateTime, Business business, Map<Integer, Integer> produseIntegerMap, String oras) {
        Command command = new Command();
        command.setData(LocalDateTime.now());
        command.setBusiness(business);
        command.setListaProduse(produseIntegerMap);
        command.setOras(oras);
        command.setAvailableInTableComanda(true);
        return command;
    }

    protected Product createProduct() {
        return createProduct(NUME_PRODUS, 5.0, IDENTIFICATOR_PRODUS, TipProdus.SAPUN);
    }

    protected Product createProduct(String numeProdus, double cantiate, String identificatorProdus, TipProdus tipProdus) {
        Product product = new Product();
        product.setNume(numeProdus);
        product.setCantitate(cantiate);
        product.setIdentificatorProdus(identificatorProdus);
        product.setTip(tipProdus);
        return product;
    }

    protected Holiday createHoliday(Employee employee, TipConcediu tipConcediu, LocalDate localDate) {
        Holiday holiday = new Holiday();
        holiday.setEmployee(employee);
        holiday.setData(localDate);
        holiday.setTipConcediu(tipConcediu);
        return holiday;
    }

    protected Price createPrice(Business business, Product product) {
        Price price = new Price();
        price.setBusiness(business);
        price.setPretUnitar(5.0);
        price.setProduct(product);
        return price;
    }

    protected User createUser() {
        return createUser(USERNAME,PASSWORD);
    }

    protected User createUser(String username, String parola) {
        User user = new User();
        user.setUsername(username);
        user.setParola(parola);
        return user;
    }

}