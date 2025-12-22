<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="ISO-8859-1">
    <title>Edit ToDo Item</title>

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
            color: #555555;z
            font-weight: bold;
        }

        .form-control {
            background-color: #ffffff;
            border: 1px solid #dddddd;
            color: #333333;
        }

        .btn-success {
            background: #007bff;
            border: none;
            color: white;
            border-radius: 30px;
            transition: 0.3s;
            padding: 0.5rem 1.5rem;
        }

        .btn-success:hover {
            background: #0056b3;
        }

        .text-center {
            margin-top: 20px;
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
            background-color: transparent;
            transition: background-color 0.3s, color 0.3s;
        }

        .status-btn.selected {
            color: #ffffff;
        }

        .status-btn[data-status="ToDo"].selected {
            background-color: #dcdada;
        }

        .status-btn[data-status="Doing"].selected {
            background-color: #ffca28;
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
    <h1 class="p-3">Edit ToDo Item</h1>

    <form:form action="/editSaveToDoItem" method="post" modelAttribute="todo">
        <!-- Hidden field for ToDo ID -->
        <form:input path="id" type="hidden"/>

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

        <!-- Repeat Frequency -->
        <div class="form-group">
            <label for="repeatFrequency" class="form-label">Repeat Frequency</label>
            <select id="repeatFrequency" name="repeatFrequency" class="form-control">
                <option value="None" <c:if test="${todo.repeatFrequency == '/'}">selected</c:if>>None</option>
                <option value="Daily" <c:if test="${todo.repeatFrequency == 'Daily'}">selected</c:if>>Daily</option>
                <option value="Weekly" <c:if test="${todo.repeatFrequency == 'Weekly'}">selected</c:if>>Weekly</option>
                <option value="Monthly" <c:if test="${todo.repeatFrequency == 'Monthly'}">selected</c:if>>Monthly</option>
                <option value="Monthly" <c:if test="${todo.repeatFrequency == 'Yearly'}">selected</c:if>>Yearly</option>
            </select>
        </div>

        <div class="mb-3">
            <label class="form-label">Status</label>
            <div>
                <button type="button" class="status-btn" data-status="ToDo">ToDo</button>
                <button type="button" class="status-btn" data-status="Doing">Doing</button>
                <button type="button" class="status-btn" data-status="Done">Done</button>
                <!-- Hidden input to store selected status -->
                <form:hidden path="status" id="status"/>
            </div>
        </div>

        <div class="text-center">
            <button type="submit" class="btn btn-success"><i class="fas fa-save"></i> Save</button>
        </div>
    </form:form>
</div>

<script>
    $(document).ready(function () {
        // Pre-select the current status based on the hidden input value
        var currentStatus = $('#status').val();
        $('.status-btn[data-status="' + currentStatus + '"]').addClass('selected');

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
        if (msg === "Edit Failure") {
            toastr.error("Something went wrong with the edit.");
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
