package slimeknights.tconstruct.library.json.math;

import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.floats.FloatStack;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;

/** Represents 2 argument stack operations */
@RequiredArgsConstructor
public enum BinaryOperator implements StackOperation {
  ADD('+') {
    @Override
    public float apply(float left, float right) {
      return left + right;
    }
  },
  SUBTRACT('-') {
    @Override
    public float apply(float left, float right) {
      return left - right;
    }
  },
  MULTIPLY('*') {
    @Override
    public float apply(float left, float right) {
      return left * right;
    }
  },
  DIVIDE('/') {
    @Override
    public float apply(float left, float right) {
      if (right == 0) {
        return 0;
      }
      return left / right;
    }
  },
  POWER('^') {
    @Override
    public float apply(float left, float right) {
      return (float)Math.pow(left, right);
    }
  };

  private final char ch;

  /** Applies this operator to the given values */
  public abstract float apply(float left, float right);

  @Override
  public void perform(FloatStack stack, float[] variables) {
    // this may throw, but that is okay as we will run this formula during parsing to make sure its valid
    // the way formulas are setup, if it does not throw during parsing, it cannot throw ever
    float right = stack.popFloat();
    float left = stack.popFloat();
    stack.push(apply(left, right));
  }


  /* JSON and network */

  /** Deserializes the operator from a character */
  public static BinaryOperator deserialize(char ch) {
    for (BinaryOperator operator : BinaryOperator.values()) {
      if (operator.ch == ch) {
        return operator;
      }
    }
    throw new JsonSyntaxException("Unknown binary operator " + ch);
  }

  @Override
  public JsonPrimitive serialize(String[] variableNames) {
    return new JsonPrimitive(ch);
  }

  @Override
  public void toNetwork(FriendlyByteBuf buffer) {
    // comment on buffer internals: the indices of this enum and StackNetworkType match up until divide,
    // so writing our ordinal allows us to read an ordinal for the other enum
    buffer.writeEnum(this);
  }
}
