create table KompiAccess (
        id bigint not null auto_increment,
        creditBalanceInSeconds integer not null,
        dailyCredit integer not null,
        dailyCreditGrantedTimestamp datetime,
        doNotTrackUntil datetime,
        lastAccess datetime,
        username varchar(255) unique,
        version bigint,
        primary key (id)
    );