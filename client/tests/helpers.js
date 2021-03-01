export const testAction = async (action, payload, state, expectedMutations, done) => {
  let count = 0;
  const commit = (type, payload) => {
    const mutation = expectedMutations[count];
    try {
      expect(type).toBe(mutation.type);
      expect(payload).toEqual(mutation.payload);
    } catch (error) {
      done(error);
    }
    count++;
    if (count >= expectedMutations.length) {
      done();
    }
  };
  const retVal = await action({ commit, state }, payload);
  if (expectedMutations.length === 0) {
    expect(count).toBe(0);
    done();
  }
  return retVal;
};
