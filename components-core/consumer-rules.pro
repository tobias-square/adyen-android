# Keep the model classes for JSON parsing.
-keep class com.adyen.checkout.components.model.** { *; }

# Keep the Component constructors for reflection initialization in the Factory.
-keepclassmembers public class * implements com.adyen.checkout.components.PaymentComponent {
   public <init>(...);
}
-keepclassmembers public class * implements com.adyen.checkout.components.ActionComponent {
   public <init>(...);
}
