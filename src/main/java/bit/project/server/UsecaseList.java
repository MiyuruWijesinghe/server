package bit.project.server;

import bit.project.server.util.security.SystemModule;

public enum UsecaseList {
    @SystemModule("User") SHOW_ALL_USERS(1),
    @SystemModule("User") SHOW_USER_DETAILS(2),
    @SystemModule("User") ADD_USER(3),
    @SystemModule("User") UPDATE_USER(4),
    @SystemModule("User") DELETE_USER(5),
    @SystemModule("User") RESET_USER_PASSWORDS(6),
    @SystemModule("Role") SHOW_ALL_ROLES(7),
    @SystemModule("Role") SHOW_ROLE_DETAILS(8),
    @SystemModule("Role") ADD_ROLE(9),
    @SystemModule("Role") UPDATE_ROLE(10),
    @SystemModule("Role") DELETE_ROLE(11),
    @SystemModule("Employee") GET_ALL_EMPLOYEES(14),
    @SystemModule("Employee") GET_BASIC_EMPLOYEES(15),
    @SystemModule("Employee") GET_EMPLOYEE(16),
    @SystemModule("Employee") ADD_EMPLOYEE(17),
    @SystemModule("Employee") UPDATE_EMPLOYEE(18),
    @SystemModule("Employee") DELETE_EMPLOYEE(19),
    @SystemModule("Customer") GET_ALL_CUSTOMERS(20),
    @SystemModule("Customer") GET_BASIC_CUSTOMERS(21),
    @SystemModule("Customer") GET_CUSTOMER(22),
    @SystemModule("Customer") ADD_CUSTOMER(23),
    @SystemModule("Customer") UPDATE_CUSTOMER(24),
    @SystemModule("Customer") DELETE_CUSTOMER(25),
    @SystemModule("Designation") GET_ALL_DESIGNATIONS(26),
    @SystemModule("Designation") ADD_DESIGNATION(27),
    @SystemModule("Designation") UPDATE_DESIGNATION(28),
    @SystemModule("Designation") DELETE_DESIGNATION(29),
    @SystemModule("Supplier") GET_ALL_SUPPLIERS(30),
    @SystemModule("Customer") GET_SUPPLIER(31),
    @SystemModule("Supplier") ADD_SUPPLIER(32),
    @SystemModule("Supplier") UPDATE_SUPPLIER(33),
    @SystemModule("Supplier") DELETE_SUPPLIER(34),
    @SystemModule("Branch") GET_ALL_BRANCHES(35),
    @SystemModule("Branch") GET_BRANCH(36),
    @SystemModule("Branch") ADD_BRANCH(37),
    @SystemModule("Branch") UPDATE_BRANCH(38),
    @SystemModule("Branch") DELETE_BRANCH(39),
    @SystemModule("Item") GET_ALL_ITEMS(40),
    @SystemModule("Item") GET_ITEM(41),
    @SystemModule("Item") ADD_ITEM(42),
    @SystemModule("Item") UPDATE_ITEM(43),
    @SystemModule("Item") DELETE_ITEM(44),
    @SystemModule("Porder") GET_ALL_PORDERS(45),
    @SystemModule("Porder") GET_PORDER(46),
    @SystemModule("Porder") ADD_PORDER(47),
    @SystemModule("Porder") UPDATE_PORDER(48),
    @SystemModule("Porder") DELETE_PORDER(49),
    @SystemModule("Purchase") GET_ALL_PURCHASES(50),
    @SystemModule("Purchase") GET_PURCHASE(51),
    @SystemModule("Purchase") ADD_PURCHASE(52),
    @SystemModule("Purchase") UPDATE_PURCHASE(53),
    @SystemModule("Purchase") DELETE_PURCHASE(54),
    @SystemModule("Inventory") GET_ALL_INVENTORIES(55),
    @SystemModule("Inventory") GET_INVENTORY(56),
    @SystemModule("Inventory") ADD_INVENTORY(57),
    @SystemModule("Inventory") UPDATE_INVENTORY(58),
    @SystemModule("Inventory") DELETE_INVENTORY(59),
    @SystemModule("Itembranch") DELETE_ITEMBRANCH(60),
    @SystemModule("Porderitem") DELETE_PORDERITEM(61),
    @SystemModule("Purchaseitem") DELETE_PURCHASEITEM(62),
    @SystemModule("Inventorycustomertype") DELETE_INVENTORYCUSTOMERTYPE(63),
    @SystemModule("Sale") GET_ALL_SALES(64),
    @SystemModule("Sale") GET_SALE(65),
    @SystemModule("Sale") ADD_SALE(66),
    @SystemModule("Sale") UPDATE_SALE(67),
    @SystemModule("Sale") DELETE_SALE(68),
    @SystemModule("Saleitem") DELETE_SALEITEM(69),
    @SystemModule("Salepayment") DELETE_SALEPAYMENT(70),
    @SystemModule("Item") GET_BASIC_ITEMS(71),
    @SystemModule("Complain") GET_ALL_COMPLAINS(72),
    @SystemModule("Complain") GET_COMPLAIN(73),
    @SystemModule("Complain") ADD_COMPLAIN(74),
    @SystemModule("Complain") UPDATE_COMPLAIN(75),
    @SystemModule("Complain") DELETE_COMPLAIN(76),
    @SystemModule("Report") SHOW_YEAR_WISE_CUSTOMER_COUNT(77),
    @SystemModule("Report") SHOW_MONTH_WISE_SALE(78),
    @SystemModule("Report") SHOW_YEAR_WISE_SALE_COUNT(79),
    @SystemModule("Report") SHOW_YEAR_WISE_INCOME(80),
    @SystemModule("Saleitem") GET_ALL_SALEITEMS(81),
    @SystemModule("Report") SHOW_YEAR_WISE_SALE(82),
    @SystemModule("Report") SHOW_YEAR_WISE_PURCHASE(83),
    @SystemModule("Report") SHOW_DAY_WISE_SALE(84),
    @SystemModule("Report") SHOW_MONTH_WISE_ITEMCATEGORY_SALE(85);


    public final int value;

    UsecaseList(int value){
        this.value = value;
    }
}
