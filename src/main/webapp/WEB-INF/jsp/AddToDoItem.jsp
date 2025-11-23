<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="ISO-8859-1">
    <title>Add ToDo Item</title>

    <!-- Modern CSS frameworks -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/toastr.min.css">

    <!-- Custom CSS for Modern Design -->
    <style>
        body {
            background-color: #ffffff;
            color: #333333;
            font-family: 'Roboto', sans-serif;
        }

        .container {
            background: #f9f9f9;
            border-radius: 16px;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
            padding: 2rem;
            margin-top: 50px;
        }

        h1 {
            text-align: center;
            color: #333333;
            font-weight: bold;
        }

        .form-label {
            color: #555555;
            font-weight: bold;
        }

        .form-control {
            background-color: #ffffff;
            border: 1px solid #dddddd;
            color: #333333;
        }

        .btn-add {
            background: #007bff;
            border: none;
            color: white;
            border-radius: 30px;
            transition: 0.3s;
            padding: 0.5rem 1.5rem;
        }

        .btn-add:hover {
            background: #0056b3;
        }

        .text-center {
            text-align: center;
        }

        /* Status button styling */
        .status-btn {
            border: 2px solid #dddddd;
            border-radius: 20px;
            padding: 0.5rem 1.5rem;
            margin: 0 5px;
            font-weight: bold;
            cursor: pointer;
            color: #333333;
            background-color: transparent; /* Start transparent */
            transition: background-color 0.3s, color 0.3s;
        }

        .status-btn.selected {
            color: #ffffff; /* Text color when selected */
        }

        .status-btn[data-status="ToDo"].selected {
            background-color: #dcdada; /* Color for ToDo */
        }

        .status-btn[data-status="Doing"].selected {
            background-color: #ffca28; /* Color for Doing */
        }

        .status-btn[data-status="Done"].selected {
            background-color: #28a745;
        }

    </style>

    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/toastr.min.js"></script>
</head>

<body>
<div class="container">
    <h1 class="p-3">Add a ToDo Item</h1>

    <form:form action="${pageContext.request.contextPath}/saveToDoItem"
               method="post" modelAttribute="todo">

    <div class="mb-3">
            <label for="title" class="form-label">Task Name</label>
            <form:input type="text" path="title" id="title" class="form-control" required="required"/>
        </div>

        <div class="mb-3">
            <label for="date" class="form-label">Date</label>
            <form:input type="date" path="date" id="date" class="form-control" required="required"/>
        </div>

        <div class="mb-3">
            <label for="time" class="form-label">Time</label>
            <form:input type="time" path="time" id="time" class="form-control" required="required"/>
        </div>

        <div class="form-group">
            <label for="repeatFrequency" class="form-label">Repeat Frequency</label>
            <select id="repeatFrequency" name="repeatFrequency" class="form-control">
                <option value="/">None</option>
                <option value="Daily">Daily</option>
                <option value="Weekly">Weekly</option>
                <option value="Monthly">Monthly</option>
                <option value="Yearly">Yearly</option>
            </select>
        </div>


        <div class="mb-3">
            <label class="form-label">Status</label>
            <div>
                <button type="button" class="status-btn" data-status="ToDo">ToDo</button>
                <button type="button" class="status-btn" data-status="Doing">Doing</button>
                <button type="button" class="status-btn" data-status="Done">Done</button>
                <!-- Hidden input to store selected status -->
                <form:hidden path="status" id="status" value="ToDo" />
            </div>
        </div>

        <div class="text-center">
            <button type="submit" class="btn-add"><i class="fas fa-plus-circle"></i> Add</button>
        </div>


    </form:form>
</div>

<script>
    $(document).ready(function () {
        // Click event for status buttons
        $('.status-btn').on('click', function () {
            // Remove 'selected' class from all buttons and add to clicked button
            $('.status-btn').removeClass('selected');
            $(this).addClass('selected');

            // Set the hidden input value based on the selected button
            $('#status').val($(this).data('status'));
        });

        // Toast message
        var msg = "${message}";
        if (msg === "Save Failure") {
            toastr.error("Something went wrong with the save.");
        }

        toastr.options = {
            "closeButton": true,
            "progressBar": true,
            "positionClass": "toast-top-right",
            "timeOut": "3000"
        };
    });
</script>
</body>

</html>