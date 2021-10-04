package ro.utcn.backend.model.enums;

/**
 * Tip concediu
 * Created by Lucian on 6/5/2017.
 */
public enum TipConcediu implements CommonEnumName {
    Libera{
        @Override
        public String getFullName() {
            return "Zi libera nationala";
        }

        @Override
        public String getMask() {
            return "";
        }
    },
    Co{
        @Override
        public String getFullName() {
            return "Concediu de odihna";
        }

        @Override
        public String getMask() {
            return "Co";
        }
    },
    Bo {
        @Override
        public String getFullName() {
            return "Boala obisnuita";
        }

        @Override
        public String getMask() {
            return "Bo";
        }
    },
    Bp {
        @Override
        public String getFullName() {
            return "Boala profesionala";
        }

        @Override
        public String getMask() {
            return "Bp";
        }
    },
    Am {
        @Override
        public String getFullName() {
            return "Accidente munca";
        }

        @Override
        public String getMask() {
            return "Am";
        }
    },
    M {
        @Override
        public String getFullName() {
            return "Maternitate";
        }

        @Override
        public String getMask() {
            return "M";
        }
    },
    I {
        @Override
        public String getFullName() {
            return "Invoiri si concedii fara salarii";
        }

        @Override
        public String getMask() {
            return "I";
        }
    },
    O {
        @Override
        public String getFullName() {
            return "Obligatii cetatenesti";
        }

        @Override
        public String getMask() {
            return "O";
        }
    },
    N {
        @Override
        public String getFullName() {
            return "Absente nemotivate";
        }

        @Override
        public String getMask() {
            return "N";
        }
    },
    Prm {
        @Override
        public String getFullName() {
            return "Program redus de maternitate";
        }

        @Override
        public String getMask() {
            return "Prm";
        }
    },
    Prb {
        @Override
        public String getFullName() {
            return "Program redus boala";
        }

        @Override
        public String getMask() {
            return "Prb";
        }
    };

    @Override
    public String toString(){
        return this.getMask() + " - " + this.getFullName();
    }
}
