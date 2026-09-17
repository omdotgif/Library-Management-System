# &#x09;Problem Statement

## Problem

Small libraries often track book issue/return using paper registers. This causes lost records,
no way to see overdue books, and no automatic fine calculation.

## Scope

A console-based Library Management System in Java that lets a librarian:

* Manage books (add, update, delete, search)
* Register members
* Issue and return books, with automatic late fines
* View reports of issued and overdue books

Data is stored in a database using JDBC, so records are saved permanently.

Out of scope: multiple branches, online member login, online payments.

## Target Users

* Librarian — main user, runs all operations
* Admin — uses the reports to track circulation and fines

## Key Features

1. Book Management
2. Member Management
3. Issue/Return with fine calculation
4. Reports (issued books, overdue books, fines collected)

