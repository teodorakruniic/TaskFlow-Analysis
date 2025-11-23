// cypress/e2e/filterTodo.cy.js

describe("TaskFlow - Filtriranje nalog (TF-FILTER-SET)", () => {

  function createTask(name, status = "ToDo") {
    cy.visit("/addToDoItem");

    cy.get("#title").clear().type(name);
    cy.get("#date").clear().type("2025-11-10");   // YYYY-MM-DD
    cy.get("#time").clear().type("14:30");

    cy.get(`.status-btn[data-status="${status}"]`).click();

    cy.get("button.btn-add").click();
    cy.url().should("include", "/viewToDoList");
  }

  function openList() {
    cy.visit("/viewToDoList");
    cy.contains("h1", "ToDo Board").should("be.visible");
  }

  before(() => {
    createTask("Filter ToDo " + Date.now(), "ToDo");
    createTask("Filter Doing " + Date.now(), "Doing");
    createTask("Filter Done " + Date.now(), "Done");
  });

  function applyStatusFilter(displayText) {
    openList();
    cy.get("#status").select(displayText);      // npr. "ToDo", "Doing", "Done", "All"
    cy.contains("button", "Filter").click();
    cy.url().should("include", "/viewToDoList");
  }

  // TF-FILTER-001 – filter To-Do
  it("TF-FILTER-001 - Filtriranje po statusu To-Do", () => {
    applyStatusFilter("ToDo");

    cy.get("table tbody tr td:nth-child(5)")
        .should("have.length.at.least", 1)
        .each(($cell) => {
          expect($cell.text().trim()).to.contain("ToDo");
        });
  });

  // TF-FILTER-002 – filter Doing
  it("TF-FILTER-002 - Filtriranje po statusu Doing", () => {
    applyStatusFilter("Doing");

    cy.get("table tbody tr td:nth-child(5)")
        .should("have.length.at.least", 1)
        .each(($cell) => {
          expect($cell.text().trim()).to.contain("Doing");
        });
  });

  // TF-FILTER-003 – filter Done
  it("TF-FILTER-003 - Filtriranje po statusu Done", () => {
    applyStatusFilter("Done");

    cy.get("table tbody tr td:nth-child(5)")
        .should("have.length.at.least", 1)
        .each(($cell) => {
          expect($cell.text().trim()).to.contain("Done");
        });
  });

  // TF-FILTER-004 – odstranitev filtra (All)
  it("TF-FILTER-004 - Odstranitev filtra (All)", () => {
    applyStatusFilter("All");

    cy.get("table tbody tr td:nth-child(5)")
        .should("have.length.at.least", 3);

    cy.get("table tbody tr td:nth-child(5)").then(($cells) => {
      const texts = [...$cells].map((td) => td.innerText.trim());
      expect(texts.join(" ")).to.contain("ToDo");
      expect(texts.join(" ")).to.contain("Doing");
      expect(texts.join(" ")).to.contain("Done");
    });
  });
});
