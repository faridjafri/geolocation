# Frontend Guide

**Location:** `src/main/resources/templates/greeting.html`

## Technology

- Thymeleaf templates
- Bootstrap 4.3.1
- jQuery 3.3.1
- Bootstrap Table 1.16.0

---

## CRITICAL: Fix UI Refresh Bug

**Problem:** Table doesn't refresh with new data after IP lookup

**Location:** AJAX success callback in greeting.html

**Fix:**

```javascript
$.ajax({
    url: '/geolocation/' + ip,
    type: 'GET',
    success: function(result) {
        if (result && result.length > 0) {
            // Add to table
            $('#geolocationTable').bootstrapTable('append', result);

            // Clear input
            $('#ipInput').val('');

            // Show success
            showMessage('Location added', 'success');
        }
    },
    error: function(xhr) {
        showMessage('Failed to fetch geolocation', 'danger');
    }
});
```

**Add message function:**

```javascript
function showMessage(text, type) {
    $('#messageArea').html(
        '<div class="alert alert-' + type + ' alert-dismissible">' +
        text +
        '<button type="button" class="close" data-dismiss="alert">&times;</button>' +
        '</div>'
    );
}
```

**Add to HTML:**

```html
<div id="messageArea"></div>
```

---

## Fix Enter Key Behavior

```javascript
$(document).ready(function() {
    $('#geolocationForm').on('submit', function(e) {
        e.preventDefault();
        lookupGeolocation();
        return false;
    });
});
```

---

## Bootstrap Table Methods

```javascript
// Load data (replace all)
$('#geolocationTable').bootstrapTable('load', dataArray);

// Append rows
$('#geolocationTable').bootstrapTable('append', dataArray);

// Remove all
$('#geolocationTable').bootstrapTable('removeAll');

// Refresh
$('#geolocationTable').bootstrapTable('refresh');

// Get data
var data = $('#geolocationTable').bootstrapTable('getData');
```

---

## Add Last N Locations Feature

**HTML:**

```html
<input type="number" id="limitInput" min="1" max="100" value="10">
<button onclick="loadLastN()">Load Recent</button>
```

**JavaScript:**

```javascript
function loadLastN() {
    var n = $('#limitInput').val();

    $.ajax({
        url: '/geolocation/history?n=' + n,
        type: 'GET',
        success: function(result) {
            $('#geolocationTable').bootstrapTable('load', result);
            showMessage('Loaded ' + result.length + ' locations', 'success');
        }
    });
}
```

---

## Add Date Range Filter

**HTML:**

```html
<input type="date" id="startDate">
<input type="date" id="endDate">
<button onclick="filterByDate()">Filter</button>
```

**JavaScript:**

```javascript
function filterByDate() {
    var start = convertToBackendFormat($('#startDate').val());
    var end = convertToBackendFormat($('#endDate').val());

    $.ajax({
        url: '/geolocation/history?start=' + start + '&end=' + end,
        success: function(result) {
            $('#geolocationTable').bootstrapTable('load', result);
        }
    });
}

function convertToBackendFormat(dateString) {
    // Convert yyyy-MM-dd to MM.dd.yyyy
    var parts = dateString.split('-');
    return parts[1] + '.' + parts[2] + '.' + parts[0];
}
```

---

## IP Validation

```javascript
function validateIP(ip) {
    var pattern = /^(?:[0-9]{1,3}\.){3}[0-9]{1,3}$/;

    if (!pattern.test(ip)) {
        return {valid: false, message: 'Invalid IP format'};
    }

    // Check octets 0-255
    var octets = ip.split('.');
    for (var i = 0; i < octets.length; i++) {
        if (parseInt(octets[i]) > 255) {
            return {valid: false, message: 'Octets must be 0-255'};
        }
    }

    return {valid: true};
}
```

---

## Loading Indicator

```html
<div id="loader" style="display:none;">
    <div class="spinner-border"></div>
</div>
```

```javascript
function lookupIP() {
    $('#loader').show();
    $('#lookupBtn').prop('disabled', true);

    $.ajax({
        // ...
        complete: function() {
            $('#loader').hide();
            $('#lookupBtn').prop('disabled', false);
        }
    });
}
```

---

## Error Handling

```javascript
error: function(xhr) {
    var message = 'An error occurred';

    if (xhr.status === 404) {
        message = 'IP not found';
    } else if (xhr.status === 400) {
        message = 'Invalid request';
    } else if (xhr.responseJSON && xhr.responseJSON.message) {
        message = xhr.responseJSON.message;
    }

    showMessage(message, 'danger');
}
```

---

## Common Pitfalls

1. **Date format** - Always convert to `MM.dd.yyyy` before sending to backend
2. **Table updates** - Use Bootstrap Table methods, don't manipulate DOM directly
3. **AJAX errors** - Always add error callback
4. **Memory leaks** - Use event delegation for dynamic elements

---

## Related Docs

- Controllers: `API-CONTROLLERS.md`
- Testing: `TESTING.md`
