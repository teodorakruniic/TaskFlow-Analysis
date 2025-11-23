// cypress/e2e/editTodo.cy.js

describe("TaskFlow - Edit ToDo item (TF-EDIT-SET)", () => {
  function createTask(name, status = "ToDo") {
    cy.visit("/addToDoItem");

    cy.get("#title").clear().type(name);
    cy.get("#date").clear().type("2025-11-10");
    cy.get("#time").clear().type("14:30");

    cy.get(`.status-btn[data-status="${status}"]`).click();

    cy.get("button.btn-add").click();
    cy.url().should("include", "/viewToDoList");

    getRowByName(name).should("exist");
  }

  function getRowByName(name) {
    return cy.get("table tbody tr").contains("td", name).parent("tr");
  }

  function applyStatusFilter(status) {
    cy.visit("/viewToDoList");
    cy.get('select[name="status"]').select(status);
    cy.contains("button", "Filter").click();
    cy.url().should("include", "/viewToDoList");
  }

  function clickEditFor(name) {
    getRowByName(name)
        .find("a[href*='/editToDoItem']")
        .click();

    cy.url().should("include", "/editToDoItem");
    cy.get("form[action='/editSaveToDoItem']").should("be.visible");
  }

  // TF-EDIT-001 – promena imena naloge
  it("TF-EDIT-001 - Urejanje imena naloge", () => {
    const originalName = "Plačaj račune EDIT-1 " + Date.now();
    const newName = "Plačaj vse račune " + Date.now();

    createTask(originalName);

    clickEditFor(originalName);

    cy.get("#title").clear().type(newName);

    cy.get("button[type='submit'], button.btn-success").first().click();

    cy.url().should("include", "/viewToDoList");

    getRowByName(newName).should("exist");
    cy.contains("table", originalName).should("not.exist");
  });

  // TF-EDIT-002 – sprememba statusa v 'Doing'
  it("TF-EDIT-002 - Sprememba statusa v 'Doing'", () => {
    const name = "Status ToDo -> Doing " + Date.now();

    createTask(name, "ToDo");

    clickEditFor(name);

    cy.get('.status-btn[data-status="Doing"]').click();

    cy.get("button[type='submit'], button.btn-success").first().click();

    cy.url().should("include", "/viewToDoList");

    applyStatusFilter("Doing");

    getRowByName(name).should("contain.text", "Doing");
  });

  // TF-EDIT-003 – označi kot dokončano (Mark Complete)
  it("TF-EDIT-003 - Označi nalogo kot 'Done' (Mark Complete)", () => {
    const name = "Mark Complete test " + Date.now();

    createTask(name, "ToDo");

    getRowByName(name)
        .find("a[href*='/updateToDoStatus']")
        .click();

    cy.url().should("include", "/viewToDoList");

    getRowByName(name).should("contain.text", "Done");
  });

  // TF-EDIT-004 – validacija praznega imena pri urejanju
  it("TF-EDIT-004 - Validacija praznega imena pri urejanju", () => {
    const name = "Invalid edit name " + Date.now();

    createTask(name);

    clickEditFor(name);

    cy.get("#title").clear();

    cy.get("button[type='submit'], button.btn-success").first().click();

    cy.get("form[action='/editSaveToDoItem']").should("be.visible");

    cy.visit("/viewToDoList");
    getRowByName(name).should("exist");
  });
});
