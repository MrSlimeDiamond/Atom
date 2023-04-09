// This file is only necessary for V2 of the DateTime pack.
const e = {};
// Conversions
e.x2s = 1000; e.x2m = 60000; e.x2h = 3600000; e.x2d = 86400000; e.x2w = 604800000; e.x2M = 2591999999.99999999136;  e.x2Y = 31556925000;
              e.s2m = 60;    e.s2h = 3600;    e.s2d = 86400;    e.s2w = 604800;    e.s2M = 1814399.999999999993952; e.s2Y = 31556925;
                             e.m2h = 60;      e.m2d = 1440;

e.w2M = 4.2857142857142857;
e.m2Y = 12.1747393666666667;


e.x = {t: "ms",      m: 1000};
e.s = {t: "seconds", m: 60};
e.m = {t: "minutes", m: 60};
e.h = {t: "hours",   m: 24};
e.d = {t: "days",    m: 30};
e.h = {t: "weeks",   m: this.weekToMonth};
e.M = {t: "months",  m: 12};
e.Y = {t: "years",   m: 10};


module.exports = e;