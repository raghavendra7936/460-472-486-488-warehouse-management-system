// Order management JavaScript
document.addEventListener('DOMContentLoaded', function() {
    const addItemBtn = document.getElementById('addItemBtn');
    const orderItemsTable = document.getElementById('orderItems');
    let inventoryItems = []; // Will be populated from the server

    // Function to load inventory items
    function loadInventoryItems() {
        console.log('Fetching inventory items...');
        return fetch('/orders/inventory')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                console.log('Fetched inventory items:', data);
                inventoryItems = data;
                return data;
            })
            .catch(error => {
                console.error('Error fetching inventory items:', error);
                alert('Error loading inventory items. Please refresh the page and try again.');
            });
    }

    // Load inventory items when the page loads
    loadInventoryItems();

    // Add item button click handler
    addItemBtn.addEventListener('click', function() {
        console.log('Add item button clicked');
        console.log('Current inventory items:', inventoryItems);

        if (inventoryItems.length === 0) {
            console.log('No inventory items available, reloading...');
            loadInventoryItems().then(() => {
                addItemRow();
            });
            return;
        }

        addItemRow();
    });

    function addItemRow() {
        const row = document.createElement('tr');
        
        // Create the select element with options
        const selectHtml = `
            <select class="form-control item-select" required>
                <option value="">Select an item</option>
                ${inventoryItems.map(item => 
                    `<option value="${item.id}" 
                     data-price="${item.unitPrice}">${item.name} (${item.quantity} available)</option>`
                ).join('')}
            </select>
        `;

        row.innerHTML = `
            <td>${selectHtml}</td>
            <td>
                <input type="number" class="form-control quantity-input" 
                       min="1" value="1" required>
            </td>
            <td>
                <input type="number" class="form-control unit-price" 
                       readonly step="0.01">
            </td>
            <td>
                <input type="number" class="form-control total-price" 
                       readonly step="0.01">
            </td>
            <td>
                <button type="button" class="btn btn-danger btn-sm remove-item">
                    <i class="bi bi-trash"></i>
                </button>
            </td>
        `;
        orderItemsTable.appendChild(row);

        // Add event listeners for the new row
        const itemSelect = row.querySelector('.item-select');
        const quantityInput = row.querySelector('.quantity-input');
        const unitPriceInput = row.querySelector('.unit-price');
        const totalPriceInput = row.querySelector('.total-price');
        const removeBtn = row.querySelector('.remove-item');

        // Update prices when item is selected
        itemSelect.addEventListener('change', function() {
            console.log('Item selected:', this.value);
            const selectedOption = this.options[this.selectedIndex];
            if (selectedOption.value) {
                const unitPrice = selectedOption.dataset.price;
                console.log('Selected item price:', unitPrice);
                unitPriceInput.value = unitPrice;
                updateTotalPrice(quantityInput, unitPrice, totalPriceInput);
            }
        });

        // Update total when quantity changes
        quantityInput.addEventListener('input', function() {
            const unitPrice = unitPriceInput.value;
            updateTotalPrice(this, unitPrice, totalPriceInput);
        });

        // Remove item row
        removeBtn.addEventListener('click', function() {
            row.remove();
            updateOrderTotal();
        });
    }

    // Helper function to update total price for an item
    function updateTotalPrice(quantityInput, unitPrice, totalPriceInput) {
        const quantity = parseFloat(quantityInput.value) || 0;
        const price = parseFloat(unitPrice) || 0;
        totalPriceInput.value = (quantity * price).toFixed(2);
        updateOrderTotal();
    }

    // Helper function to update the overall order total
    function updateOrderTotal() {
        const totalInputs = document.querySelectorAll('.total-price');
        let total = 0;
        totalInputs.forEach(input => {
            total += parseFloat(input.value) || 0;
        });
        // Update the hidden total amount input
        const totalAmountInput = document.getElementById('totalAmount');
        if (totalAmountInput) {
            totalAmountInput.value = total.toFixed(2);
        }
    }

    // Form submission handler
    const orderForm = document.getElementById('addOrderForm');
    orderForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        // Validate form data
        const customerId = document.getElementById('customer').value;
        if (!customerId) {
            alert('Please select a customer');
            return;
        }

        const rows = orderItemsTable.querySelectorAll('tr');
        if (rows.length === 0) {
            alert('Please add at least one item to the order');
            return;
        }

        let hasValidItems = false;
        const orderItems = [];

        // Collect and validate order items
        rows.forEach(row => {
            const itemSelect = row.querySelector('.item-select');
            const quantityInput = row.querySelector('.quantity-input');
            
            if (itemSelect.value && quantityInput.value) {
                hasValidItems = true;
                orderItems.push({
                    inventory: {
                        id: parseInt(itemSelect.value)
                    },
                    quantity: parseInt(quantityInput.value),
                    unitPrice: parseFloat(row.querySelector('.unit-price').value),
                    totalPrice: parseFloat(row.querySelector('.total-price').value)
                });
            }
        });

        if (!hasValidItems) {
            alert('Please select at least one item and specify quantity');
            return;
        }

        const orderData = {
            customer: {
                id: parseInt(customerId)
            },
            orderItems: orderItems,
            totalAmount: parseFloat(document.getElementById('totalAmount').value),
            status: 'PENDING'
        };

        console.log('Submitting order data:', orderData);

        // Send order data to server
        fetch('/orders', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(orderData)
        })
        .then(async response => {
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Server error: ${errorText}`);
            }
            return response.json();
        })
        .then(data => {
            console.log('Order created successfully:', data);
            // Close modal and refresh page
            const modal = bootstrap.Modal.getInstance(document.getElementById('addOrderModal'));
            modal.hide();
            window.location.reload();
        })
        .catch(error => {
            console.error('Error creating order:', error);
            alert('Error creating order: ' + error.message);
        });
    });
}); 