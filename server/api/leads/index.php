<?php
/**
 * BoilerCalc Lead API Endpoint
 *
 * Accepts POST requests with JSON lead data from the Android app
 * and sends a formatted email to Premium-gas@mail.ru
 *
 * Deploy to: kotelgavno.ru/public_html/api/leads/index.php
 */

// ---- Debug (remove in production) ----
ini_set('display_errors', 1);
error_reporting(E_ALL);

// ---- Configuration ----
$RECIPIENT_EMAIL = 'Premium-gas@mail.ru';
$FROM_EMAIL      = 'noreply@kotelgavno.ru';
$FROM_NAME       = 'BoilerCalc App';
$SUBJECT_PREFIX  = 'Новая заявка на котёл';

// ---- CORS & Headers ----
header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit;
}

// Only allow POST
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(['error' => 'Method not allowed'], JSON_UNESCAPED_UNICODE);
    exit;
}

// ---- Read & Parse JSON Body ----
$rawBody = file_get_contents('php://input');
$data = json_decode($rawBody, true);

if (!$data) {
    http_response_code(400);
    echo json_encode(['error' => 'Invalid JSON'], JSON_UNESCAPED_UNICODE);
    exit;
}

// ---- Extract Fields ----
$name       = trim($data['name'] ?? '');
$phone      = trim($data['phone'] ?? '');
$region     = trim($data['region'] ?? '');
$context    = trim($data['context'] ?? '');
$model      = trim($data['model'] ?? '');
$boilerType = trim($data['boilerType'] ?? '');
$pressure   = $data['pressure'] ?? null;
$capacity   = $data['capacity'] ?? null;
$utm        = $data['utm'] ?? [];
$timestamp  = trim($data['timestamp'] ?? '');

// ---- Validate Required Fields ----
if ($name === '' || $phone === '') {
    http_response_code(400);
    echo json_encode(['error' => 'Name and phone are required'], JSON_UNESCAPED_UNICODE);
    exit;
}

// ---- Build Email ----
$subject = "$SUBJECT_PREFIX — $name";

// Format date
$dateStr = date('d.m.Y H:i:s');
if ($timestamp !== '' && is_numeric($timestamp)) {
    $dateStr = date('d.m.Y H:i:s', (int)($timestamp / 1000));
}

// Build HTML body
$html = "
<html>
<head><meta charset='utf-8'></head>
<body style='font-family: Arial, sans-serif; padding: 20px;'>
<h2 style='color: #1a73e8;'>Новая заявка из приложения BoilerCalc</h2>
<table style='border-collapse: collapse; width: 100%; max-width: 600px;'>
    <tr style='background: #f5f5f5;'>
        <td style='padding: 10px; border: 1px solid #ddd; font-weight: bold;'>Имя</td>
        <td style='padding: 10px; border: 1px solid #ddd;'>" . htmlspecialchars($name) . "</td>
    </tr>
    <tr>
        <td style='padding: 10px; border: 1px solid #ddd; font-weight: bold;'>Телефон</td>
        <td style='padding: 10px; border: 1px solid #ddd;'>" . htmlspecialchars($phone) . "</td>
    </tr>
    <tr style='background: #f5f5f5;'>
        <td style='padding: 10px; border: 1px solid #ddd; font-weight: bold;'>Город / регион</td>
        <td style='padding: 10px; border: 1px solid #ddd;'>" . htmlspecialchars($region ?: '—') . "</td>
    </tr>";

if ($model !== '') {
    $html .= "
    <tr>
        <td style='padding: 10px; border: 1px solid #ddd; font-weight: bold;'>Модель котла</td>
        <td style='padding: 10px; border: 1px solid #ddd;'>" . htmlspecialchars($model) . "</td>
    </tr>";
}

if ($boilerType !== '') {
    $typeLabel = $boilerType === 'steam' ? 'Паровой' : ($boilerType === 'water' ? 'Водогрейный' : htmlspecialchars($boilerType));
    $html .= "
    <tr style='background: #f5f5f5;'>
        <td style='padding: 10px; border: 1px solid #ddd; font-weight: bold;'>Тип котла</td>
        <td style='padding: 10px; border: 1px solid #ddd;'>$typeLabel</td>
    </tr>";
}

if ($pressure !== null) {
    $html .= "
    <tr>
        <td style='padding: 10px; border: 1px solid #ddd; font-weight: bold;'>Давление, бар</td>
        <td style='padding: 10px; border: 1px solid #ddd;'>$pressure</td>
    </tr>";
}

if ($capacity !== null) {
    $html .= "
    <tr style='background: #f5f5f5;'>
        <td style='padding: 10px; border: 1px solid #ddd; font-weight: bold;'>Производительность</td>
        <td style='padding: 10px; border: 1px solid #ddd;'>$capacity</td>
    </tr>";
}

$html .= "
    <tr>
        <td style='padding: 10px; border: 1px solid #ddd; font-weight: bold;'>Источник</td>
        <td style='padding: 10px; border: 1px solid #ddd;'>" . htmlspecialchars($context ?: 'app') . "</td>
    </tr>
    <tr style='background: #f5f5f5;'>
        <td style='padding: 10px; border: 1px solid #ddd; font-weight: bold;'>Дата</td>
        <td style='padding: 10px; border: 1px solid #ddd;'>$dateStr</td>
    </tr>";

// UTM parameters
if (!empty($utm)) {
    $utmStr = '';
    foreach ($utm as $key => $val) {
        $utmStr .= htmlspecialchars($key) . '=' . htmlspecialchars($val) . '; ';
    }
    $html .= "
    <tr>
        <td style='padding: 10px; border: 1px solid #ddd; font-weight: bold;'>UTM метки</td>
        <td style='padding: 10px; border: 1px solid #ddd;'>$utmStr</td>
    </tr>";
}

$html .= "
</table>
<p style='color: #888; font-size: 12px; margin-top: 20px;'>
    Отправлено из мобильного приложения BoilerCalc (Premium Gas Company)
</p>
</body>
</html>";

// ---- Send Email ----
$headers  = "MIME-Version: 1.0\r\n";
$headers .= "Content-Type: text/html; charset=utf-8\r\n";
$headers .= "From: $FROM_NAME <$FROM_EMAIL>\r\n";
$headers .= "Reply-To: $FROM_EMAIL\r\n";
$headers .= "X-Mailer: BoilerCalc-Server/1.0\r\n";

$sent = mail($RECIPIENT_EMAIL, $subject, $html, $headers);

if ($sent) {
    // Log successful submission
    $logLine = date('Y-m-d H:i:s') . " | OK | $name | $phone | $region | $model\n";
    @file_put_contents(__DIR__ . '/leads.log', $logLine, FILE_APPEND | LOCK_EX);

    http_response_code(200);
    echo json_encode(['status' => 'ok'], JSON_UNESCAPED_UNICODE);
} else {
    // Log failure
    $logLine = date('Y-m-d H:i:s') . " | FAIL | $name | $phone | mail() returned false\n";
    @file_put_contents(__DIR__ . '/leads.log', $logLine, FILE_APPEND | LOCK_EX);

    http_response_code(500);
    echo json_encode(['error' => 'Failed to send email'], JSON_UNESCAPED_UNICODE);
}
