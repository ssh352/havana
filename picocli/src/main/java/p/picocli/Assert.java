package p.picocli;

final class Assert
{
    static void check()
    {
        boolean enabled = false;
        assert enabled = true;
        if (!enabled)
            throw new AssertionError("assert not enabled");
    }

    private Assert()
    {
    }
}
