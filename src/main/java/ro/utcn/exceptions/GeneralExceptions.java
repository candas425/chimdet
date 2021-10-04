package ro.utcn.exceptions;

/**
 * General exceptions
 * <p>
 * Created by Lucian on 4/25/2017.
 */
public class GeneralExceptions extends Exception {

    //parser exceptions
    public static final String SETEAZA_UN_CRITERIU_DE_CAUTRARE_IN_SETARI = "Seteaza un criteriu de cautare in Setari";
    public static final String SETEAZA_UN_CONTOR_PANA_LA_CANTIATE_IN_SETARI = "Seteaza un contor pana la locatia cantitati aflata in fisierul excel in Setari";
    public static final String NUMELE_FISIERULUI_NU_ESTE_IN_LISTA_DE_ORASE_DIN_SETARI = "Nu a fost gasit numele fisierului in lista de orase din Setari";

    //setari exceptions
    public static final String LISTA_DE_ORASE_NU_ESTE_SETATA_IN_SETARI = "Lista de orase nu este setata in Setari";
    public static final String PARAMETRU_SCALARE_X_EXCEPTION = "Seteaza parametru de scalare X in Setari";
    public static final String PARAMETRU_SCALARE_Y_EXCEPTION = "Seteaza parametru de scalare Y in Setari";
    public static final String PARAMETRU_TRANSLATARE_X_EXCEPTION = "Seteaza parametru de translatare X in Setari";
    public static final String PARAMETRU_TRANSLATARE_Y_EXCEPTION = "Seteaza parametru de translatare Y in Setari";
    public static final String PARAMETRU_TABEL_HEIGHT_EXCEPTION = "Seteaza parametru de inaltime a tabelului in Setari";
    public static final String VALOARE_TVA_EXCEPTION = "Seteaza valoarea TVA-ului in Setari";
    public static final String DIMENSIUNE_COLOANA_PRODUS_EXCEPTION = "Seteaza dimensiunea coloanei numelui produsului in Setari";
    public static final String DIMENSIUNE_NUMAR_IDENTIFICATOR_PRODUS_EXCEPTION = "Seteaza dimensiunea coloanei numelui produsului in Setari";
    public static final String DIMENSIUNE_COLOANA_CANTITATE_EXCEPTION = "Seteaza dimensiunea coloanei de numar identificator a produsului in Setari";
    public static final String LISTA_CANTITATI_PRODUSE_DISPONIBILE_EXCEPTION = "Lista de cantitati nu este setata in Setari";
    public static final String CONTINUT_EMAIL_EXCEPTION = "Seteaza continutul email-ului in Setari";

    //printare
    public static final String PRINT_FAILED = "Printarea nu a reusit";
    public static final String PRINT_SUCCESS = "In curs de printare...";

    //importare fisier
    public static final String IMPORT_FAILED = "Fisierul trebuie sa contina 'NUME PRODUS | CANTITATE | TIP PRODUS (SAPUN,DETERGENT | COD)' pe fiecare rand";
    public static final String IMPORT_PRET_FAILED = "Fisierul trebuie sa contina 'NUME PRODUS | VALOARE' pe fiecare rand";

    //angajati
    public static final String ZI_LIBERA_GRESITA = "Ziua de concediu/libera nu poate sa fie sambata sau duminica";
    public static final String LIPSA_FISIER_PONTAJ = "Fisierul pontaj.xlsx lipseste din folderul de files";
    public static final String FISIERUL_DE_PONTAJ_NU_ARE_DATE_SUFICIENTE = "Lipsesc informatii din fisierul de pontaj";
    public static final String FISIERUL_EXCEL_CONTINE_PREA_MULTI_ANGAJATI = "Fisierul excel contine prea multe detalii";
    public static final String MAXIM_31_ZILE = "Diferenta dintre data de sfarsit si data de inceput nu trebuie sa fie mai mare de 31 zile";

    //comenzi
    public static final String LIPSA_FISIER_DECLARATIE_1 = "Fisierul declartie1.docx lipseste din folderul de files";
    public static final String LIPSA_FISIER_DECLARATIE_2 = "Fisierul declartie2.docx lipseste din folderul de files";

    //mail
    public static final String MAIL_SUCCESS = "Mail-ul a fost trimis cu succes";

    public GeneralExceptions(String message) {
        super(message);
    }
}
