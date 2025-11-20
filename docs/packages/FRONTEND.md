# Frontend Guide

**Location:** `src/main/resources/templates/`

## Files

- `greeting.html` - Main UI with Bootstrap Table

---

## CRITICAL: Fix UI Refresh Bug

**Problem:** Table doesn't update after IP lookup

**Fix in greeting.html AJAX success callback:**

```javascript
success: function(result) {
    if (result && result.length > 0) {
        $('#geolocationTable').bootstrapTable('append', result);
        $('#ipInput').val('');
        showMessage('Location added', 'success');
    }
}
```

**Add message function:**

```javascript
function showMessage(text, type) {
    $('#messageArea').html(
        '<div class="alert alert-' + type + ' alert-dismissible">' +
        text + '<button type="button" class="close" data-dismiss="alert">&times;</button></div>'
    );
}
```

**Add to HTML:** `<div id="messageArea"></div>`

---

## Fix Enter Key

```javascript
$('#geolocationForm').on('submit', function(e) {
    e.preventDefault();
    lookupGeolocation();
    return false;
});
```

---

## Bootstrap Table Methods

```javascript
$('#geolocationTable').bootstrapTable('append', data);  // Add rows
$('#geolocationTable').bootstrapTable('load', data);    // Replace all
$('#geolocationTable').bootstrapTable('removeAll');     // Clear
```

---

## Date Format Conversion

Backend uses `MM.dd.yyyy`:

```javascript
function convertToBackendFormat(dateString) {
    // yyyy-MM-dd → MM.dd.yyyy
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
        return {valid: false, message: 'Invalid IP'};
    }
    return {valid: true};
}
```
