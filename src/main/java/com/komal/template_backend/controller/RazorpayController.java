package com.komal.template_backend.controller;

import com.komal.template_backend.model.Donourentity;
import com.komal.template_backend.repo.DonationRepo;
import com.komal.template_backend.service.DonationService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payment")
public class RazorpayController {

    @Value("${razorpay.key_id}")
    private String keyId;

    @Value("${razorpay.key_secret}")
    private String keySecret;
    @Autowired
    DonationService donationService;

    // Rough India bounding box check
    private boolean coordsInIndia(Double lat, Double lon) {
        if (lat == null || lon == null) return false;
        return lat >= 6.5 && lat <= 35.5 && lon >= 68.0 && lon <= 97.5;
    }

//    @PostMapping("/create-order")
//    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> body) {
//        try {
//            Object amtObj = body.getOrDefault("amount", 500);
//            int amount;
//            if (amtObj instanceof Number) {
//                amount = ((Number) amtObj).intValue();
//            } else if (amtObj instanceof String) {
//                amount = Integer.parseInt((String) amtObj);
//            } else {
//                amount = 500;
//            }
//
//            // Optional location check (same as before)
//            Map<String, Object> location = (Map<String, Object>) body.get("location");
//            Double lat = null, lon = null;
//            String city = null, state = null;
//            if (location != null) {
//                if (location.get("lat") != null) lat = ((Number) location.get("lat")).doubleValue();
//                if (location.get("lon") != null) lon = ((Number) location.get("lon")).doubleValue();
//                city = (String) location.get("city");
//                state = (String) location.get("state");
//            }
//
//            if (lat != null && lon != null && !coordsInIndia(lat, lon)) {
//                return ResponseEntity.badRequest().body(Map.of(
//                        "success", false,
//                        "message", "Payments allowed only from India."
//                ));
//            }
//
//            // --- Create order in Razorpay ---
//            RazorpayClient client = new RazorpayClient(keyId, keySecret);
//            JSONObject orderRequest = new JSONObject();
//            orderRequest.put("amount", amount * 100);
//            orderRequest.put("currency", "INR");
//            orderRequest.put("receipt", "txn_" + System.currentTimeMillis());
//            orderRequest.put("payment_capture", 1);
//
//            JSONObject notes = new JSONObject();
//            if (city != null) notes.put("city", city);
//            if (state != null) notes.put("state", state);
//            if (lat != null) notes.put("lat", lat);
//            if (lon != null) notes.put("lon", lon);
//            orderRequest.put("notes", notes);
//
//            Order order = client.orders.create(orderRequest);
//
//            // --- Save only minimal pending order (no personal info) ---
//            Donourentity donor = new Donourentity();
//            donor.setOrderId(order.get("id"));
//            donor.setAmount((double) amount);
//            donor.setStatus("PENDING");
//            donor.setDonationDate(LocalDateTime.now());
//            donationRepo.save(donor); // No encryption here, just track orderId
//
//            // --- Response to frontend ---
//            JSONObject response = new JSONObject();
//            response.put("id", Optional.ofNullable(order.get("id")));
//            response.put("amount", Optional.ofNullable(order.get("amount")));
//            response.put("currency", Optional.ofNullable(order.get("currency")));
//            response.put("keyId", keyId);
//            response.put("message", "Order created successfully.");
//
//            return ResponseEntity.ok(response.toMap());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500)
//                    .body(Map.of("success", false, "message", "Server error: " + e.getMessage()));
//        }
//    }

//    @PostMapping("/create-order")
//    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> body) {
//        try {
//            // --- Extract amount safely ---
//            Object amtObj = body.getOrDefault("amount", 500);
//            int amount;
//            if (amtObj instanceof Number) {
//                amount = ((Number) amtObj).intValue();
//            } else if (amtObj instanceof String) {
//                amount = Integer.parseInt((String) amtObj);
//            } else {
//                amount = 500;
//            }
//
//            // --- Extract location (optional) ---
//            Map<String, Object> location = (Map<String, Object>) body.get("location");
//            Double lat = null, lon = null;
//            String city = null, state = null;
//            if (location != null) {
//                if (location.get("lat") != null) lat = ((Number) location.get("lat")).doubleValue();
//                if (location.get("lon") != null) lon = ((Number) location.get("lon")).doubleValue();
//                city = (String) location.get("city");
//                state = (String) location.get("state");
//            }
//
//            // --- India-only check ---
//            if (lat != null && lon != null && !coordsInIndia(lat, lon)) {
//                return ResponseEntity.badRequest().body(Map.of(
//                        "success", false,
//                        "message", "Payments allowed only from India."
//                ));
//            }
//
//            // --- Create order with Razorpay ---
//            RazorpayClient client = new RazorpayClient(keyId, keySecret);
//            JSONObject orderRequest = new JSONObject();
//            orderRequest.put("amount", amount * 100);
//            orderRequest.put("currency", "INR");
//            orderRequest.put("receipt", "txn_" + System.currentTimeMillis());
//            orderRequest.put("payment_capture", 1);
//
//            JSONObject notes = new JSONObject();
//            if (city != null) notes.put("city", city);
//            if (state != null) notes.put("state", state);
//            if (lat != null) notes.put("lat", lat);
//            if (lon != null) notes.put("lon", lon);
//            orderRequest.put("notes", notes);
//
//            Order order = client.orders.create(orderRequest);
//
//            // --- 🧩 Save minimal donor info with orderId (status=PENDING) ---
//            Donourentity donor = new Donourentity();
//            donor.setAmount((double) amount);
//            donor.setOrderId(order.get("id"));  // important for later verification
//            donor.setStatus("PENDING");
//            donor.setDonationDate(java.time.LocalDateTime.now());
//
//            donationService.saveDonation(donor);
//
//            // --- Build response for frontend ---
//            JSONObject response = new JSONObject();
//            response.put("id", Optional.ofNullable(order.get("id")));
//            response.put("amount", Optional.ofNullable(order.get("amount")));
//            response.put("currency", Optional.ofNullable(order.get("currency")));
//            response.put("keyId", keyId);
//            response.put("message", "Order created and donor saved.");
//
//            return ResponseEntity.ok(response.toMap());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500)
//                    .body(Map.of("success", false, "message", "Server error: " + e.getMessage()));
//        }
//    }
@PostMapping("/create-order")
public ResponseEntity<?> createOrder(@RequestBody Donourentity donor) {
    try {
//        if (donor.getAmount() == null || donor.getAmount() <= 0) {
//            donor.setAmount(500.0); // fallback default
//        }

        // ✅ Create Razorpay Order
        RazorpayClient client = new RazorpayClient(keyId, keySecret);
        JSONObject options = new JSONObject();
        options.put("amount", donor.getAmount() * 100); // paise
        options.put("currency", "INR");
        options.put("receipt", "receipt_" + System.currentTimeMillis());
        options.put("payment_capture", 1);

        Order order = client.orders.create(options);

        // ✅ Attach orderId + set pending
        donor.setOrderId(order.get("id"));
        donor.setStatus("PENDING");
        donor.setDonationDate(LocalDateTime.now());

        // ✅ Save donor details (with encryption inside your service)
        donationService.saveDonation(donor);

        // ✅ Send response to frontend
        return ResponseEntity.ok(Map.of(
                "success", true,
                "id", order.get("id"),
                "amount", donor.getAmount() * 100,
                "currency", "INR",
                "keyId", keyId,
                "message", "Order created successfully"
        ));
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500)
                .body(Map.of("success", false, "message", "Server error: " + e.getMessage()));
    }
}

    @Autowired
    private DonationRepo donationRepo;

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, Object> body) {
        try {
            System.out.println("🟡 Received /verify payload: " + body);

            // Extract fields
            String razorpayOrderId = (String) body.get("razorpay_order_id");
            String razorpayPaymentId = (String) body.get("razorpay_payment_id");
            String razorpaySignature = (String) body.get("razorpay_signature");

            // Basic validation
            if (razorpayOrderId == null || razorpayPaymentId == null || razorpaySignature == null) {
                System.err.println("❌ Missing one or more fields in verify payload");
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Missing one or more required fields"
                ));
            }

            // ✅ Verify Signature
            String payload = razorpayOrderId + "|" + razorpayPaymentId;
            String generatedSignature = hmacSha256(payload, keySecret);

            System.out.println("🧩 Signature payload: " + payload);
            System.out.println("🧩 Generated: " + generatedSignature);
            System.out.println("🧩 Received: " + razorpaySignature);

            if (!generatedSignature.equals(razorpaySignature)) {
                System.err.println("❌ Invalid signature received");
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Invalid signature"
                ));
            }

            // ✅ Fetch Razorpay Payment Details
            RazorpayClient client = new RazorpayClient(keyId, keySecret);
            com.razorpay.Payment payment = client.payments.fetch(razorpayPaymentId);
            JSONObject paymentJson = payment.toJson();

            String status = paymentJson.getString("status"); // captured, failed, refunded
            String method = paymentJson.optString("method", "UNKNOWN");
            String bank = paymentJson.optString("bank", "");
            String vpa = paymentJson.optString("vpa", "");
            int amount = paymentJson.getInt("amount"); // in paise
            String currency = paymentJson.getString("currency");

            // ✅ Update Donor record
            Optional<Donourentity> donorOpt = donationRepo.findByOrderId(razorpayOrderId);
            if (donorOpt.isPresent()) {
                Donourentity donor = donorOpt.get();
                donor.setPaymentId(razorpayPaymentId);
                donor.setSignature(razorpaySignature);
                donor.setStatus(status.equalsIgnoreCase("captured") ? "SUCCESS" : status.toUpperCase());
                donor.setPaymentMethod(method);
                donor.setBankName(bank.isEmpty() ? vpa : bank);
                donor.setAmount(amount / 100.0); // Convert paise to rupees
                donor.setDonationDate(LocalDateTime.now());

                donationService.saveDonation(donor);
                System.out.println("✅ Donor updated successfully");
            } else {
                System.err.println("⚠️ Donor not found for orderId: " + razorpayOrderId);
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "status", status,
                    "method", method,
                    "amount", amount / 100.0,
                    "currency", currency
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Server error: " + e.getMessage()
            ));
        }
    }

    private String hmacSha256(String data, String secret) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] hash = sha256_HMAC.doFinal(data.getBytes());

        // Convert to HEX string (Razorpay uses hex encoding)
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
    // Utility method for signature verification
//    private String hmacSha256(String data, String secret) throws Exception {
//        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
//        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
//        sha256Hmac.init(secretKey);
//        byte[] hash = sha256Hmac.doFinal(data.getBytes());
//        return new String(Base64.getEncoder().encode(hash));
//    }
//}
//
//    @PostMapping("/verify")
//    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, Object> body) {
//        try {
//            System.out.println("Received /verify payload: " + body);
//            String razorpayOrderId = (String) body.get("razorpay_order_id");
//            String razorpayPaymentId = (String) body.get("razorpay_payment_id");
//            String razorpaySignature = (String) body.get("razorpay_signature");
//            if (razorpayOrderId == null || razorpayPaymentId == null || razorpaySignature == null) {
//                return ResponseEntity.badRequest().body(Map.of(
//                        "success", false,
//                        "message", "Missing one or more required fields"
//                ));
//            }
//            // Verify signature
//            String payload = razorpayOrderId + "|" + razorpayPaymentId;
//            String generatedSignature = hmacSha256(payload, keySecret);
//            System.out.println("Signature payload: " + payload);
//            System.out.println("Generated: " + generatedSignature);
//            System.out.println("Received: " + razorpaySignature);
//
//            if (!generatedSignature.equals(razorpaySignature)) {
//                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Invalid signature"));
//            }
//
//            // ✅ Fetch Razorpay payment details
//            RazorpayClient client = new RazorpayClient(keyId, keySecret);
////            com.razorpay.Payment payment = client.payments.fetch(razorpayPaymentId);
//            com.razorpay.Payment payment = client.payments.fetch(razorpayPaymentId);
//            JSONObject paymentJson = payment.toJson();
//            String status =  paymentJson.getString("status"); // captured, failed, refunded
//            String method = paymentJson.optString("method", "UNKNOWN");
//            String bank = paymentJson.optString("bank", "");
//            String vpa = paymentJson.optString("vpa", "");
//            int amount = paymentJson.getInt("amount"); // in paise
//            String currency = paymentJson.getString("currency");
//
//            // ✅ Find donor by orderId and update
//            Optional<Donourentity> donorOpt = donationRepo.findByOrderId(razorpayOrderId);
//            if (donorOpt.isPresent()) {
//                Donourentity donor = donorOpt.get();
//                donor.setPaymentId(razorpayPaymentId);
//                donor.setSignature(razorpaySignature);
//                donor.setStatus(status.equalsIgnoreCase("captured") ? "SUCCESS" : status.toUpperCase());
//                donor.setPaymentMethod(method);
//                donor.setBankName(bank);
//                donor.setAmount(amount / 100.0); // convert paise to rupees
//                donor.setDonationDate(LocalDateTime.now());
//
//                donationService.saveDonation(donor);
//            } else {
//                System.err.println("⚠️ Donor not found for orderId: " + razorpayOrderId);
//            }
//            return ResponseEntity.ok(Map.of(
//                    "success", true,
//                    "status", status,
//                    "method", method,
//                    "amount", amount / 100.0,
//                    "currency", currency
//            ));
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Server error: " + e.getMessage()));
//        }
//    }
//    private String hmacSha256(String data, String secret) throws Exception {
//        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
//        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256");
//        sha256_HMAC.init(secret_key);
//        byte[] hash = sha256_HMAC.doFinal(data.getBytes("UTF-8"));
//        // Razorpay returns hex lowercase; compute hex
//        StringBuilder sb = new StringBuilder();
//        for (byte b : hash) {
//            sb.append(String.format("%02x", b));
//        }
//        return sb.toString();
//    }

// package com.komal.template_backend.controller;

// import com.komal.template_backend.model.Donourentity;
// import com.komal.template_backend.repo.DonationRepo;
// import com.komal.template_backend.service.DonationService;
// import com.razorpay.Order;
// import com.razorpay.RazorpayClient;
// import com.razorpay.Subscription;
// import org.json.JSONArray;
// import org.json.JSONObject;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import javax.crypto.Mac;
// import javax.crypto.spec.SecretKeySpec;
// import java.time.LocalDateTime;
// import java.util.Base64;
// import java.util.Map;
// import java.util.Optional;
// import com.razorpay.Utils;

// @RestController
// @RequestMapping("/api/payment")
// public class RazorpayController {

//     @Value("${razorpay.key_id}")
//     private String keyId;

//     @Value("${razorpay.key_secret}")
//     private String keySecret;
//     @Value("${razorpay.variable_plan_id}")
//     private String variablePlanId;
//     @Value("${razorpay.webhook_secret}")
//     private String webhookSecret ;
//     @Autowired
//     DonationService donationService;

//     // Rough India bounding box check
//     private boolean coordsInIndia(Double lat, Double lon) {
//         if (lat == null || lon == null) return false;
//         return lat >= 6.5 && lat <= 35.5 && lon >= 68.0 && lon <= 97.5;
//     }
// @PostMapping("/create-order")
// public ResponseEntity<?> createOrder(@RequestBody Donourentity donor) {
//     try {


//         // ✅ Create Razorpay Order
//         RazorpayClient client = new RazorpayClient(keyId, keySecret);
//         JSONObject options = new JSONObject();
//         options.put("amount", donor.getAmount() * 100); // paise
//         options.put("currency", "INR");
//         options.put("receipt", "receipt_" + System.currentTimeMillis());
//         options.put("payment_capture", 1);

//         Order order = client.orders.create(options);

//         // ✅ Attach orderId + set pending
//         donor.setOrderId(order.get("id"));
//         donor.setStatus("PENDING");
//         donor.setDonationDate(LocalDateTime.now());

//         // ✅ Save donor details (with encryption inside your service)
//         donationService.saveDonation(donor);

//         // ✅ Send response to frontend
//         return ResponseEntity.ok(Map.of(
//                 "success", true,
//                 "id", order.get("id"),
//                 "amount", donor.getAmount() * 100,
//                 "currency", "INR",
//                 "keyId", keyId,
//                 "message", "Order created successfully"
//         ));
//     } catch (Exception e) {
//         e.printStackTrace();
//         return ResponseEntity.status(500)
//                 .body(Map.of("success", false, "message", "Server error: " + e.getMessage()));
//     }
// }
//     @Autowired
//     private DonationRepo donationRepo;

//     @PostMapping("/verify")
//     public ResponseEntity<?> verifyPayment(@RequestBody Map<String, Object> body) {
//         try {
//             System.out.println("🟡 Received /verify payload: " + body);

//             // Extract fields
//             String razorpayOrderId = (String) body.get("razorpay_order_id");
//             String razorpayPaymentId = (String) body.get("razorpay_payment_id");
//             String razorpaySignature = (String) body.get("razorpay_signature");

//             // Basic validation
//             if (razorpayOrderId == null || razorpayPaymentId == null || razorpaySignature == null) {
//                 System.err.println("❌ Missing one or more fields in verify payload");
//                 return ResponseEntity.badRequest().body(Map.of(
//                         "success", false,
//                         "message", "Missing one or more required fields"
//                 ));
//             }

//             // ✅ Verify Signature
//             String payload = razorpayOrderId + "|" + razorpayPaymentId;
//             String generatedSignature = hmacSha256(payload, keySecret);

//             System.out.println("🧩 Signature payload: " + payload);
//             System.out.println("🧩 Generated: " + generatedSignature);
//             System.out.println("🧩 Received: " + razorpaySignature);

//             if (!generatedSignature.equals(razorpaySignature)) {
//                 System.err.println("❌ Invalid signature received");
//                 return ResponseEntity.badRequest().body(Map.of(
//                         "success", false,
//                         "message", "Invalid signature"
//                 ));
//             }

//             // ✅ Fetch Razorpay Payment Details
//             RazorpayClient client = new RazorpayClient(keyId, keySecret);
//             com.razorpay.Payment payment = client.payments.fetch(razorpayPaymentId);
//             JSONObject paymentJson = payment.toJson();

//             String status = paymentJson.getString("status"); // captured, failed, refunded
//             String method = paymentJson.optString("method", "UNKNOWN");
//             String bank = paymentJson.optString("bank", "");
//             String vpa = paymentJson.optString("vpa", "");
//             int amount = paymentJson.getInt("amount"); // in paise
//             String currency = paymentJson.getString("currency");

//             // ✅ Update Donor record
//             Optional<Donourentity> donorOpt = donationRepo.findByOrderId(razorpayOrderId);
//             if (donorOpt.isPresent()) {
//                 Donourentity donor = donorOpt.get();
//                 donor.setPaymentId(razorpayPaymentId);
//                 donor.setSignature(razorpaySignature);
//                 donor.setStatus(status.equalsIgnoreCase("captured") ? "SUCCESS" : status.toUpperCase());
//                 donor.setPaymentMethod(method);
//                 donor.setBankName(bank.isEmpty() ? vpa : bank);
//                 donor.setAmount(amount / 100.0); // Convert paise to rupees
//                 donor.setDonationDate(LocalDateTime.now());

//                 donationService.saveDonation(donor);
//                 System.out.println("✅ Donor updated successfully");
//             } else {
//                 System.err.println("⚠️ Donor not found for orderId: " + razorpayOrderId);
//             }
//             return ResponseEntity.ok(Map.of(
//                     "success", true,
//                     "status", status,
//                     "method", method,
//                     "amount", amount / 100.0,
//                     "currency", currency
//             ));
//         } catch (Exception e) {
//             e.printStackTrace();
//             return ResponseEntity.status(500).body(Map.of(
//                     "success", false,
//                     "message", "Server error: " + e.getMessage()
//             ));
//         }
//     }
//     private String hmacSha256(String data, String secret) throws Exception {
//         Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
//         SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
//         sha256_HMAC.init(secret_key);
//         byte[] hash = sha256_HMAC.doFinal(data.getBytes());

//         // Convert to HEX string (Razorpay uses hex encoding)
//         StringBuilder hexString = new StringBuilder();
//         for (byte b : hash) {
//             String hex = Integer.toHexString(0xff & b);
//             if (hex.length() == 1) hexString.append('0');
//             hexString.append(hex);
//         }
//         return hexString.toString();
//     }
//     // STEP 1 — Save donor before subscription
//     @PostMapping("/create-donor-record")
//     public ResponseEntity<?> createDonorRecord(@RequestBody Donourentity donor) {
//         try {
//             donor.setStatus("PENDING");
//             donor.setDonationDate(LocalDateTime.now());
//             Donourentity saved = donationService.saveDonation(donor);
//             return ResponseEntity.ok(Map.of("success", true, "donorId", saved.getId()));
//         } catch (Exception e) {
//             e.printStackTrace();
//             return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
//         }
//     }
// //    // STEP 2 — Create subscription using SAME plan
// //    @PostMapping("/create-subscription")
// //    public ResponseEntity<?> createSubscription(@RequestBody Map<String, Object> req) {
// //        try {
// //            Integer amount = (Integer) req.get("amount"); // rupees
// //            String donorId = (String) req.get("donorId");
// //
// //            if (amount < 100 || amount > 10000) {
// //                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Amount must be 100–10000"));
// //            }
// //            RazorpayClient client = new RazorpayClient(keyId, keySecret);
// //            JSONObject subReq = new JSONObject();
// //            subReq.put("plan_id", variablePlanId);
// //            subReq.put("total_count", 120);
// //            subReq.put("customer_notify", 1);
// //
// //            // ADDON for actual monthly amount
// //            JSONArray addons = new JSONArray();
// //            JSONObject addon = new JSONObject();
// //            JSONObject item = new JSONObject();
// //            item.put("amount", amount * 100);
// //            item.put("currency", "INR");
// //            addon.put("item", item);
// //            addons.put(addon);
// //            subReq.put("addons", addons);
// //
// //            // store donor id
// //            JSONObject notes = new JSONObject();
// //            notes.put("donorId", donorId);
// //            subReq.put("notes", notes);
// //
// //            Subscription sub = client.subscriptions.create(subReq);
// //
// //            // Save subscriptionId in DB now
// //            Optional<Donourentity> opt = donationRepo.findById(donorId);
// //            if (opt.isPresent()) {
// //                Donourentity d = opt.get();
// //                d.setSubscriptionId(sub.get("id"));
// //                d.setSubscriptionStatus("PENDING");
// //                d.setAmount(amount.doubleValue());
// //                d.setPlanId(variablePlanId);
// //                donationService.saveDonation(d);
// //            }
// //
// //            return ResponseEntity.ok(Map.of(
// //                    "success", true,
// //                    "subscription_id", sub.get("id"),
// //                    "short_url", sub.toJson().optString("short_url")
// //            ));
// //
// //        } catch (Exception e) {
// //            e.printStackTrace();
// //            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
// //        }
// //    }


// //    // STEP 3 — Webhook
// //    @PostMapping("/razorpay-webhook")
// //    public ResponseEntity<?> handleWebhook(
// //            @RequestBody String payload,
// //            @RequestHeader(value = "X-Razorpay-Signature", required = false) String signature) {
// //
// //        try {
// //            if (!Utils.verifyWebhookSignature(payload, signature, webhookSecret)) {
// //                return ResponseEntity.status(400).body("Invalid webhook signature");
// //            }
// //
// //            JSONObject json = new JSONObject(payload);
// //            String event = json.getString("event");
// //
// //            System.out.println("🔔 Webhook Event: " + event);
// //
// //            switch (event) {
// //
// //                case "subscription.activated": {
// //                    JSONObject entity = json.getJSONObject("payload").getJSONObject("subscription").getJSONObject("entity");
// //                    String subscriptionId = entity.getString("id");
// //                    String donorId = entity.getJSONObject("notes").optString("donorId");
// //
// //                    donationRepo.findById(donorId).ifPresent(d -> {
// //                        d.setSubscriptionStatus("ACTIVE");
// //                        try {
// //                            donationService.saveDonation(d);
// //                        } catch (Exception e) {
// //                            throw new RuntimeException(e);
// //                        }
// //                    });
// //                    break;
// //                }
// //
// //                case "subscription.charged": {
// //                    JSONObject entity = json.getJSONObject("payload").getJSONObject("payment").getJSONObject("entity");
// //                    String subId = entity.getString("subscription_id");
// //                    String paymentId = entity.getString("id");
// //
// //                    donationRepo.findBySubscriptionId(subId).ifPresent(d -> {
// //                        d.setPaymentId(paymentId);
// //                        try {
// //                            donationService.saveDonation(d);
// //                        } catch (Exception e) {
// //                            throw new RuntimeException(e);
// //                        }
// //                    });
// //                    break;
// //                }
// //
// //                case "mandate.authorized": {
// //                    System.out.println("Mandate authorized");
// //                    break;
// //                }
// //            }
// //
// //            return ResponseEntity.ok("OK");
// //
// //        } catch (Exception e) {
// //            e.printStackTrace();
// //            return ResponseEntity.status(500).body("Webhook error");
// //        }
// //    }
// @PostMapping("/create-subscription")
// public ResponseEntity<?> createSubscription(@RequestBody Donourentity donor) {
//     try {
//         RazorpayClient client = new RazorpayClient(keyId, keySecret);

//         JSONObject options = new JSONObject();
//         options.put("plan_id", "plan_M123XYZ");  // your variable plan id
//         options.put("total_count", 12);          // 12 months
//         options.put("quantity", donor.getAmount() * 100);  // ₹ to paise

//         // ⭐ very important: attach donorId
//         JSONObject notes = new JSONObject();
//         notes.put("donorId", donor.getId());   // YOUR MONGO DB ID
//         options.put("notes", notes);

//         com.razorpay.Subscription sub = client.subscriptions.create(options);

//         // save subscriptionId in donor record
//         donor.setSubscriptionId(sub.get("id"));
//         donor.setSubscriptionStatus("CREATED");
//         donationRepo.save(donor);

//         return ResponseEntity.ok(Map.of(
//                 "success", true,
//                 "subscription_id", sub.get("id"),
//                 "short_url", sub.toJson().optString("short_url")
//         ));

//     } catch (Exception e) {
//         e.printStackTrace();
//         return ResponseEntity.status(500).body("Error: " + e.getMessage());
//     }
// }

//     @PostMapping("/razorpay-webhook")
// public ResponseEntity<?> handleWebhook(
//         @RequestBody String payload,
//         @RequestHeader("X-Razorpay-Signature") String signature) {

//     try {
//         // 1️⃣ Verify Signature
//         if (!Utils.verifyWebhookSignature(payload, signature, webhookSecret)) {
//             System.out.println("❌ Invalid Webhook Signature");
//             return ResponseEntity.status(400).body("Invalid signature");
//         }

//         JSONObject json = new JSONObject(payload);
//         String event = json.getString("event");

//         System.out.println("🔔 Webhook Event Received: " + event);

//         // -------------------------------
//         // 2️⃣ Subscription ACTIVATED
//         // -------------------------------
//         if (event.equals("subscription.activated")) {

//             JSONObject entity = json
//                     .getJSONObject("payload")
//                     .getJSONObject("subscription")
//                     .getJSONObject("entity");

//             String subscriptionId = entity.getString("id");
//             String donorId = entity.getJSONObject("notes").optString("donorId");

//             donationRepo.findById(donorId).ifPresent(d -> {
//                 d.setSubscriptionId(subscriptionId);
//                 d.setSubscriptionStatus("ACTIVE");
//                 donationRepo.save(d);
//             });

//             return ResponseEntity.ok("Subscription Activated");
//         }

//         // -------------------------------
//         // 3️⃣ Subscription Auto Payment CHARGED
//         // -------------------------------
//         if (event.equals("subscription.charged")) {

//             JSONObject entity = json
//                     .getJSONObject("payload")
//                     .getJSONObject("payment")
//                     .getJSONObject("entity");

//             String subscriptionId = entity.getString("subscription_id");
//             String paymentId = entity.getString("id");
//             double amount = entity.getInt("amount") / 100.0;

//             donationRepo.findBySubscriptionId(subscriptionId).ifPresent(d -> {
//                 d.setPaymentId(paymentId);
//                 d.setAmount(amount);
//                 d.setDonationDate(LocalDateTime.now());
//                 d.setStatus("SUCCESS");
//                 donationRepo.save(d);
//             });

//             return ResponseEntity.ok("Subscription Charged");
//         }

//         // -------------------------------
//         // 4️⃣ Mandate Authorized
//         // -------------------------------
//         if (event.equals("mandate.authorized")) {
//             System.out.println("✔ Mandate successfully authorized");
//             return ResponseEntity.ok("Mandate authorized");
//         }

//         // -------------------------------
//         // 5️⃣ Subscription Halted / Cancelled
//         // -------------------------------
//         if (event.equals("subscription.halted") || event.equals("subscription.cancelled")) {

//             JSONObject entity = json
//                     .getJSONObject("payload")
//                     .getJSONObject("subscription")
//                     .getJSONObject("entity");

//             String subscriptionId = entity.getString("id");

//             donationRepo.findBySubscriptionId(subscriptionId).ifPresent(d -> {
//                 d.setSubscriptionStatus("HALTED");
//                 donationRepo.save(d);
//             });

//             return ResponseEntity.ok("Subscription Halted");
//         }

//         return ResponseEntity.ok("Unhandled Event");
//     }
//     catch (Exception e) {
//         e.printStackTrace();
//         return ResponseEntity.status(500).body("Webhook error");
//     }
// }

// }



