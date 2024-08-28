<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="Model.TransactionBean" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Transaction History</title>
    <link rel="shortcut icon" href="img/logonct.png" type="image/x-icon">
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .table-custom {
            border: 1px solid #dee2e6;
            border-collapse: collapse;
        }
        .table-custom th, .table-custom td {
            border: 1px solid #dee2e6;
        }
        .album-table {
            margin-bottom: 0;
        }
        .album-table th, .album-table td {
            border: none;
            background-color: #f8f9fa;
        }
    </style>
</head>

<body>
    <div class="container mt-5">
        <h2>Transaction History</h2>
        <table class="table table-custom">
            <thead>
                <tr>
                    <th rowspan="2">Order ID</th>
                    <th rowspan="2">Total Price</th>
                    <th rowspan="2">Order Date</th>
                    <th rowspan="2">Payment Method</th>
                </tr>
                <tr>
                    <th>Album Name</th>
                    <th>Quantity</th>
                </tr>
            </thead>
            <tbody>
                <% 
                    List<TransactionBean> transactionList = (List<TransactionBean>) request.getAttribute("transactionList");
                    if (transactionList != null && !transactionList.isEmpty()) {
                        for (TransactionBean transaction : transactionList) {
                            String[] albumNames = transaction.getAlbumNames().split(", ");
                            String[] quantities = transaction.getQuantities().split(", ");
                %>
                <tr>
                    <td rowspan="<%= albumNames.length %>"><%= transaction.getOrderId() %></td>
                    <td><%= albumNames[0] %></td>
                    <td><%= quantities[0] %></td>
                    <td rowspan="<%= albumNames.length %>"><%= transaction.getTotalPrice() %></td>
                    <td rowspan="<%= albumNames.length %>"><%= transaction.getOrderDate() %></td>
                    <td rowspan="<%= albumNames.length %>"><%= transaction.getPaymentMethod() %></td>
                </tr>
                <% for (int i = 1; i < albumNames.length; i++) { %>
                <tr>
                    <td><%= albumNames[i] %></td>
                    <td><%= quantities[i] %></td>
                </tr>
                <% } %>
                <%
                        }
                    } else {
                %>
                <tr>
                    <td colspan="6">No transactions found</td>
                </tr>
                <%
                    }
                %>
            </tbody>
        </table>
        <a href="index.jsp" class="btn btn-primary">Back to Home</a>
    </div>

    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.3/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</body>
</html>