// cypress/e2e/createTodo.cy.js

describe("TaskFlow - Create ToDo item (TF-CREATE-SET)", () => {
  function openAddForm() {
    cy.visit("/addToDoItem");
    cy.url().should("include", "/addToDoItem");
  }

  // TF-CREATE-001 – minimalno veljavna naloga
  it("TF-CREATE-001 - Ustvarjanje minimalno veljavne naloge", () => {
    openAddForm();

    cy.get("#title").type("Plačaj račune");

    cy.get("#date").type("2025-11-10");
    cy.get("#time").type("14:30");

    cy.get("button.btn-add").click();

    cy.url().should("include", "/viewToDoList");

    cy.get("table tbody tr")
        .contains("td", "Plačaj račune")
        .parent("tr")
        .should("contain.text", "ToDo");
  });

  // TF-CREATE-002 – vsi podatki izpolnjeni
  it("TF-CREATE-002 - Ustvarjanje naloge z vsemi polji", () => {
    const name = "Poročilo Q4";

    openAddForm();

    cy.get("#title").type(name);

    cy.get("#date").type("2025-11-10");
    cy.get("#time").type("14:30");

    cy.get('.status-btn[data-status="ToDo"]').click();
    cy.get('select[name="repeatFrequency"]').select("Monthly");

    cy.get("button.btn-add").click();

    cy.url().should("include", "/viewToDoList");


    cy.get("table tbody tr")
        .contains("td", name)
        .parent("tr")
        .should("contain.text", "10.11.2025")  // prikazani format
        .and("contain.text", "14:30")
        .and("contain.text", "ToDo");
  });

  // TF-CREATE-003 – prazen *Name*
  it("TF-CREATE-003 - Validacija praznega imena", () => {
    openAddForm();

    cy.get("#title").should("have.value", "");

    cy.get("#date").type("2025-11-10");
    cy.get("#time").type("14:30");

    cy.get("button.btn-add").click();

    cy.url().should("include", "/addToDoItem");
    cy.url().should("not.include", "/viewToDoList");
  });

  // TF-CREATE-004 – neveljaven datum/čas
  it("TF-CREATE-004 - Validacija datuma in časa", () => {
    openAddForm();

    cy.get("#title").type("Neveljaven datum/čas");


    cy.get("#date").invoke("val", "2025-13-40");
    cy.get("#time").invoke("val", "25:61");

    cy.get("button.btn-add").click();

    cy.url().should("include", "/addToDoItem");
    cy.url().should("not.include", "/viewToDoList");
  });
});
