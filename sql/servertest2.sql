drop user c##servertest2 cascade;
--계정생성
CREATE USER c##servertest2 IDENTIFIED BY servertest2
    DEFAULT TABLESPACE users
    TEMPORARY TABLESPACE temp
    PROFILE DEFAULT;
--권한부여
GRANT CONNECT, RESOURCE TO c##servertest2;
GRANT CREATE VIEW, CREATE SYNONYM TO c##servertest2;
GRANT UNLIMITED TABLESPACE TO c##servertest2;
--락 풀기
ALTER USER c##servertest2 ACCOUNT UNLOCK;

CREATE TABLE product(
    product_id number,
    pname varchar2(30),
    quantity number,
    price number);
    
ALTER table product add Constraint product_product_id_pk primary key (product_id);     